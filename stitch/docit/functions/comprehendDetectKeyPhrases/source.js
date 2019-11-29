exports = async function comprehendKeyPhrasesAsync(){
  
  const aws = "docit";  // The name we gave to our configured AWS Service
  const comprehend = context.services.get(aws).comprehend();
  
  const dataAccessRoleArn  = 'arn:aws:iam::613372475800:role/service-role/AmazonComprehendServiceRole-awsreinventcomprehends3'; 
  const inputDataConfig = {
    "InputFormat": "ONE_DOC_PER_FILE",
    "S3Uri": "s3://mdb-eng-reinvent-demos/enron/maildir/"
  };
  const outputDataConfig ={
      "S3Uri": "s3://mdb-eng-reinvent-demos/"
    };
  const langCode='en';

  let createDocRequest = {
    DataAccessRoleArn: dataAccessRoleArn,
    InputDataConfig: inputDataConfig,
    LanguageCode: langCode,
    OutputDataConfig: outputDataConfig
  };
  
  
  // { "status": "ok",
  //   "result": {
  //       "JobId": "42f216c4d631c8de8adf1abe35701375",
  //       "JobStatus": "SUBMITTED"
  //   }
  // }
  
  return comprehend.StartKeyPhrasesDetectionJob(createDocRequest)
      .then(cResult => {
        return {status: 'ok', result: cResult};
      })
      .catch(cError => {
        return {status: 'err', error: cError.message, name: cError.name};
      });
  
}