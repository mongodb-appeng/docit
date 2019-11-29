// Uploads an image to s3
exports = async function uploadImage(b64EncodedImg, fn, type) {
  
  const binaryImage = BSON.Binary.fromBase64(b64EncodedImg, 0);
  
  const bucket = "mdb-eng-reinvent-demos"; // S3 Bucket Name
  const region = "us-west-2";   // S3 Bucket Region
  const aws = "docit";  // The name we gave to our configured AWS Service
  
  const s3 = context.services.get(aws).s3(region);
  
  return s3.PutObject({
    Bucket: bucket,
    Key: fn,
    ContentType: type,
    Body: binaryImage
  }).then(ok => {
    return {status: 'ok', results: ok};
  }).catch(err => {
    return {status: 'err', result: JSON.stringify(err)};
  });
}