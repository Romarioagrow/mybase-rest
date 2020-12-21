import axios from "axios";

const request01 = '/auth/check'

export function showInfo() {
    console.log('ClientApiService Info')
}

export function doRequest(url, data, config) {
    switch (url) {

        case request01 : return doGetRequest(url, data);

    }
}

function doGetRequest(url, config) {
    axios.get(url, config).then(response => {
        return response;
    })
}

function doPostRequest(url, data, config) {
    axios.post(url, data, config).then(response => {
        return response;
    })
}

