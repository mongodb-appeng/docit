exports = function(chartId, filter=null, expiryTime=300, autorefresh=300){
  const CHARTS_URL = context.values.get('CHARTS_BASE_URL');
  const TENANT = context.values.get('CHARTS_TENANT_ID');
  const SIGNING_KEY = context.values.get('EMBEDDING_SIGNING_KEY');
  
  const timestamp = Math.floor(Date.now()/1000);
  let payload = `id=${chartId}&tenant=${TENANT}&timestamp=${timestamp}&expires-in=${expiryTime}`;
  
  if(filter){
    payload += `&filter=${encodeURIComponent(JSON.stringify(filter))}`;
  }
  
  payload += `&autorefresh=${autorefresh}`;
  
  const signature = utils.crypto.hmac(payload, SIGNING_KEY, 'sha256', 'hex');
  return `${CHARTS_URL}/embed/charts?${payload}&signature=${signature}`;
};