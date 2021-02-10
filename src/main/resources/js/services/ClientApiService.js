import axios from "axios";

const hasAuthRequest = '/api/user/auth/check'

export function showInfo() {
    console.log('ClientApiService Info')
}

export function doRequest(url, data, config) {
    switch (url) {

        case hasAuthRequest : return doGetRequest(url, data);

    }
}

export function hasAuth() {
    return doGetRequest(hasAuthRequest)
}

function doGetRequest(url, config) {
    axios.get(url, config).then(response => {
        return extractResponseData(response);
    })
}

function doPostRequest(url, data, config) {
    axios.post(url, data, config).then(response => {
        return extractResponseData(response);
    })
}

function extractResponseData(response) {
    const responseData = response.data
    console.log('extractResponseData', responseData)
    return responseData
}

