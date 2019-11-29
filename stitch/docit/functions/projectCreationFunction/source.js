/*
 * This trigger will execute when a project is created or updated, which must go through
 * the following states:
 * (1) create CSV file for training a classifier and move it to S3 and update project, known as CREATION_STATE
 * (2) Once S3 CSV file is created, begin training a classifier, known as TRAINING_STATE
 * (3) Finally, from TRAINING_STATE, we move to READY_TO_CLASSIFY_PROJECT
 *
 * NOTE:
 * Currently we only do the initial creation state (1) in this demo as we are still working
 * through the requirements of building a successful classifier
 *
 * async/await is used to control flow where required
 *
 */
exports = async function(changeEvent) {
  if(changeEvent.operationType === 'insert'){
    // stage (1)
    
    /*
     * setup required stage (1) variables
     */
    const fullDocument = changeEvent.fullDocument;
    const S3_BUCKET = context.values.get('DOCIT_S3_BASE_BUCKET');
    const s3Service = context.services.get('docit').s3('us-east-1');
    const comprehendService = context.services.get('docit').comprehend();
    const projectsColl = context.services.get('mongodb-atlas').db('docit').collection('projects');
    const docsColl = context.services.get('mongodb-atlas').db('docit').collection('documents');
    const casesColl = context.services.get('mongodb-atlas').db('docit').collection('cases')
    
    /*
     * from project name & case name create a csv file name
     */
    const caseDoc = await casesColl.findOne({'_id': fullDocument.caseId}, {_id: 0, name: 1})
    const csvFileName = caseDoc.name.toLowerCase() + '-' + fullDocument.name.toLowerCase().replace(/ /gi, '-') + '.csv';
     
     /*
      * from full document get interesting & uninteresting elements
      */
     const interesting = await docsColl.find({_id: {$in: fullDocument.interestingIds}}, {_id: 0, content: 1}).toArray();
     const uninteresting = await docsColl.find({_id: {$in: fullDocument.uninterestingIds}}, {_id: 0, content: 1}).toArray();
     
     /*
      * get these documents from the documents collection & create string blob LABEL,CONTENT\n
      */
     let i = 0;
     let csvFileContent = ''
     for(i = 0; i < interesting.length; i++){
       csvFileContent = csvFileContent + 'INTERESTING,' + interesting[i].content.replace(/\r\n/gi, ' ').replace(/\r/gi, ' ').replace(/\n/gi, ' ') + '\n'
     }
     
     for(i = 0; i < uninteresting.length; i++){
       csvFileContent = csvFileContent + 'UNINTERESTING,' + uninteresting[i].content.replace(/\r\n/gi, ' ').replace(/\r/gi, ' ').replace(/\n/gi, ' ') + '\n'
     }
    
     /*
      * write to S3
      */
     return s3Service.PutObject({
       Bucket: S3_BUCKET,
       Key: csvFileName,
       ContentType: 'type/text',
       Body: csvFileContent
     })
     .then(etagOutput => {
       /*
        * move project to next state
        */
       return projectsColl.updateOne({
         _id: fullDocument._id},
         {$set: {
           'classifier.trainingFile': csvFileName,
           'classifier.s3file': 's3://' + S3_BUCKET + '/' + csvFileName,
           'classifier.s3etag': etagOutput.ETag.replace(/"/gi, ''),
           status: 'needs training'
         }}
       )
       .then(response => {
         console.log('success: ' + JSON.stringify(response));
         return {status: 'ok', resp: response};
       })
       .catch(err => {
         console.log('update error: ' + JSON.stringify(err));
         return {status: 'error', msg: 'failed to update document', error: err};
       })
     })
     .catch(err => {
       console.log('s3 put error: ' + JSON.stringify(err));
       return {status: 'error', msg: 'failed tp save s3 object', error: err};
     })
  } else if(changeEvent.operationType === 'update'){
    // stage (2) & (3) // TODO //
    console.log('pass on update at this time');
  }
};
