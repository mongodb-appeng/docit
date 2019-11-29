/*
  TODO: CURRENTLY NOT IMPLEMENTED!
  
    This function will be run when the client SDK 'callResetPasswordFunction' and is called with an object parameter
    which contains four keys: 'token', 'tokenId', 'username', and 'password'.

    The return object must contain a 'status' key which can be empty or one of three string values: 
      'success', 'pending', or 'fail'

    'success': the user's password is set to the passed in 'password' parameter.

    'pending': the user's password is not reset and the UserPasswordAuthProviderClient 'resetPassword' function would 
      need to be called with the token, tokenId, and new password via an SDK. (see below)

      const emailPassClient = Stitch.defaultAppClient.auth
        .getProviderClient(UserPasswordAuthProviderClient.factory);

      emailPassClient.resetPassword(token, tokenId, newPassword)

    'fail': the user's password is not reset and will not be able to log in with that password.

    If an error is thrown within the function the result is the same as 'fail'.
    
    The default function provided below will always result in failure.
*/
exports = async ({ token, tokenId, username, password }) => {
  const userColl = context.services.get('mongodb-atlas').db('docit').collection('users');
  
  const user = await userColl.findOne({_id: 'username'})
  
  // process the confirm token, tokenId, username and password
  return { status: 'fail' };
  }
