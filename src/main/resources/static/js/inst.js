const bizSdk = require('facebook-nodejs-business-sdk');

const accessToken = 'EAAHxq3fekZA4BAFtoe3zVgWrObSanTvdsNwQFCeZBByGkhosJkOYukow3jkzUWlIHTbD1RwvkLIupiDNSdsKV2PJnfmxNQA0WGU2gMVHtG6mxGOnCcBlqVG6X1u8zMPG1LOUZANwxlAZCPwrrVMaTE5NwXTHCapxkprJrhcFXVIs1Coeg7bKKAazfjr8ov3hZAYoBFYUIrgZDZD';
const accountId = 'act_{2128921037200382}';

const FacebookAdsApi = bizSdk.FacebookAdsApi.init(accessToken);
const AdAccount = bizSdk.AdAccount;
const Campaign = bizSdk.Campaign;

const account = new AdAccount(accountId);
var campaigns;

account.read([AdAccount.Fields.name])
    .then((account) =>{
        return account.getCampaigns([Campaign.Fields.name], { limit: 10 }) // fields array and params
    })
    .then((result) =>{
        campaigns = result
        campaigns.forEach((campaign) =>console.log(campaign.name))
    }).catch(console.error);