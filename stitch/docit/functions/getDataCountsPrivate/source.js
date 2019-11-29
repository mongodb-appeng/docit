exports = function(arg){
  const coll = context.services.get('mongodb-atlas').db('docit').collection('dataCounts');
  return coll.aggregate([
    {$match: {
      _id: {$nin: arg.exclusionList},
      type: arg.searchType
    }},
    {$sort: {total: -1}},
    {$limit: 10},
    {$addFields: {totalStr: {$toString: '$total'}}},
    {$project: {
      name: {$concat: ['$_id', ' (', '$totalStr', ')']},
      type: 1,
      _id: 1,
      total: 1
    }}
  ]).toArray()
    .then(results => {
      console.log('private result');
      console.log(JSON.stringify(results))
      return {status: 'ok', result: results};
    })
    .catch(error => {
      console.log('private error');
      console.log(JSON.stringify(error));
      return {status: 'err', result: error};
    })
};