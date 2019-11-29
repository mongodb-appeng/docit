/*
 * this is called by a trigger every 10 minutes to build out all entites that 
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
        {$project: {_id: 0, 'analysis.comprehend.entities': 1}},
        {$unwind: '$analysis.comprehend.entities'},
        {$group: {_id: '$analysis.comprehend.entites.text', total: {$sum: 1}, entityTypes: {$addToSet: '$analysis.comprehend.entities.type'}}},
        {$addFields: {type: 'ENTITIES', case: {_id: c._id, name: c.name}}},
        {$merge: {into: 'dataCounts', on: '_id', whenMatched: 'replace', whenNotMatched: 'insert'}}
      ]
    ).toArray()
      .then(r => { return {status: 'ok'}})
      .catch(e => { return {status: 'err', err: e}});
  })
};