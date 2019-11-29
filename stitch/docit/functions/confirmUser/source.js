/*
 * confirm registered user, the username (email) must exist in the database and can only
 * be added by an administrator of the system
 */
exports = async ({ token, tokenId, username }) => {
  console.log('confirmUser(): username=', username, ', token=', token, ', tokenId=', tokenId);
  const userColl = context.services.get('mongodb-atlas').db('docit').collection('users');
  
  const user = await userColl.findOne({_id: username});
  if(user.hasOwnProperty('_id') && user._id === username){
    return { status: 'success' };
  } else {
    return { status: 'fail' };
  }
}