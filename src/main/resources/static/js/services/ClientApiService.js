import axios from "axios";

const request01 = '/api/user/auth/check'

export function showInfo() {
    console.log('ClientApiService Info')
}

export function doRequest(url, data, config) {
    switch (url) {

        case request01 : return doGetRequest(url, data);

    }
}

export function hasAuth() {
    //const apiUrl = ''
    console.log('hasAuth')
    return doGetRequest(request01)
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

