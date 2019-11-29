exports = async function(args){
  
  
  const dColl = context.services.get('mongodb-atlas').db('docit').collection('documents');
  const pipeline = [];
  pipeline.push({$searchBeta: {index: 'fts', search: { query: args.q, path: args.p}}});
  if(args.project){
    pipeline.push({$project: {_id: 1, score: {$meta: 'searchScore'}}})
  } else {
    pipeline.push({$addFields: {score: {$meta: 'searchScore'}}})
  }
  pipeline.push({$limit: args.l})
  pipeline.push({$sort: {score: -1}})
  return dColl.aggregate(pipeline).toArray()
    .then(r => { return {status: 'ok', result: r}})
    .catch(e => {
      return {status: 'err', err: e};
    });
};