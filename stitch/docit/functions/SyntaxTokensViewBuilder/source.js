/*
 * this is called by a trigger every 10 minutes to build out all the PROPN syntaxTokens that 
 * exist in the dataset for a given case
 */
exports = async function() {
  const dColl = context.services.get('mongodb-atlas').db('docit').collection('documents');
  const cColl = context.services.get('mongodb-atlas').db('docit').collection('cases');
  
  const cases = await cColl.find().toArray();
  
  return cases.forEach(c => {
    dColl.aggregate(
      [
        {$match: {'case._id': c._id}},
        {$project: {_id: 0, 'syntaxTokens': {$filter: {input: '$analysis.comprehend.syntaxTokens', as: 'token', cond: {$eq: ['$$token.partOfSpeech.tag', 'PROPN']}}}}},
        {$unwind: '$syntaxTokens'},
        {$group: {_id: '$syntaxTokens.text', total: {$sum: 1}}},
        {$addFields: {type: 'SYNTAX_TOKENS', case: {_id: c._id, name: c.name}}},
        {$merge: {into: 'dataCounts', on: '_id', whenMatched: 'replace', whenNotMatched: 'insert'}}
      ],
      {allowDiskUse: true}
    ).toArray()
      .then(r => { return {status: 'ok'}})
      .catch(e => { return {status: 'err', err: e}});
  })
};
