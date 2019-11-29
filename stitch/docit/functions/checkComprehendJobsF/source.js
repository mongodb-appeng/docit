exports = async function checkComprehendJobsF(){
  
  const aws = "docit";  // The name we gave to our configured AWS Service
  const comprehend = context.services.get(aws).comprehend();
  const collection = context.services.get('mongodb-atlas').db("docit").collection('comprehendStatus');

   kpJobs = await comprehend.ListKeyPhrasesDetectionJobs({})
      .then(cResult => {
        return {status: 'ok', result: cResult};
      })
      .catch(cError => {
        return {status: 'err', error: cError.message, name: cError.name};
      });
      
  
    if (kpJobs.status === 'ok') {
      kpJobs.result.KeyPhrasesDetectionJobPropertiesList.forEach(function(job) {
        console.log(JSON.stringify(job));
        collection.updateOne({ "JobId": job.JobId }, {"$set": job}, { "upsert": true});
    });
    } else {
      console.log(kpJobs.error);
    }
    
    // TODO Add other jobs lists
    return {status: 'ok'};
}