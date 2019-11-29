// Extracts text form a given S3 file (PNG, JPEG or PDF)
exports = function(fn, ds='Enron Email Corpus'){
  
  const bucket = "mdb-eng-reinvent-demos"; // S3 Bucket Name
  const aws = "docit";  // The name we gave to our configured AWS Service
  const textract = context.services.get(aws).textract();
  
  
  // using synchronous option here
  return textract.DetectDocumentText({
    Document: {
      S3Object: {
        Bucket: bucket,
        Name: fn
      }
    }
  }).then(docResult => {
    
    // parse results
    let results = { 
      content :'', 
      source: 'textract', 
      analysis: {
       textract: docResult
     }
    };
    for(let i = 0; i < docResult.Blocks.length; i++){
      docResult.content = docResult.content + docResult.Blocks[i].text.trim();
    }
    
    // return results, caller inserts them to MongoDB Atlas
    return {status: 'ok', result: results};
  }).catch(err => {
    return {status: 'err', result: JSON.stringify(err)};
  });
};