/*
 * this is called by a trigger every 10 minutes to build out all key phrases that 
 * exist in the dataset for a given case
 * TODO: this currently cannot be run by the trigger due to: {allowDiskUse: true}
 *       need to work with Product Management to understand possible solutions
 */
exports = async function() {
  const dColl = context.services.get('mongodb-atlas').db('docit').collection('documents');
  const cColl = context.services.get('mongodb-atlas').db('docit').collection('cases');
  
  const cases = await cColl.find().toArray();
  
  return cases.forEach(c => {
    return dColl.aggregate(
      [
        {$match: {'case._id': c._id}},
        {$project: {_id: 0, 'analysis.comprehend.keyPhrases': 1}},
        {$unwind: '$analysis.comprehend.keyPhrases'},
        {$group: {_id: '$analysis.comprehend.keyPhrases.text', total: {$sum: 1}}},
        {$addFields: {type: 'KEY_PHRASES', case: {_id: c._id, name: c.name}}},
        {$merge: {into: 'dataCounts', on: '_id', whenMatched: 'replace', whenNotMatched: 'insert'}}
      ]
    ).toArray()
      .then(r => { return {status: 'ok'}})
      .catch(e => { return {status: 'err', err: e}});
  })
};
