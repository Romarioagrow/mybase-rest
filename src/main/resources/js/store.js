import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

const clientApiService = require('services/ClientApiService.js');

export default new Vuex.Store({
    state: {



        userAccount: {
            hasServerAuthorisation: false,
            currentProfile: null,
            instProfile: null,
            instFollowers: null
        }

    },

    mutations: {
        setCurrentProfile(currentState, currentProfile) {
            currentState.userAccount.currentProfile = currentProfile
            currentState.userAccount.hasServerAuthorisation = true
        },

        /*setCurrentProfile(currentState, currentProfile) {
            currentState.userAccount.currentProfile = currentProfile
            currentState.userAccount.hasServerAuthorisation = true
        },*/


        setInstFollowers(currentState, instFollowers) {
            currentState.userAccount.instFollowers = instFollowers
        },
        setInstProfile(currentState, profile) {
            currentState.userAccount.instProfile = profile
        },

        clearUser(currentState) {
            currentState.userAccount.hasServerAuthorisation = false
            currentState.userAccount.currentProfile = null
            currentState.userAccount.instProfile = null
            currentState.userAccount.instFollowers = null
        }
    },

    getters: {
        userAuth: state =>
            state.userAccount
            && state.userAccount.currentProfile
            && state.userAccount.hasServerAuthorisation
    },

    actions: {

        setUserAuthorization(context, userResponseData) {
            context.commit('setCurrentProfile', userResponseData)
        },


        clearUserAuthorization(context) {
            context.commit('clearUser')
        },


        authUser(context, userDto) {
            console.log('authUser(user)', userDto)
            context.commit('setCurrentProfile', userDto)

            /*this.state.userAccount.currentProfile = userDto;
            this.state.userAccount.hasServerAuthorisation = true;*/
        },


        isUserAuth() {
            console.log('this.state.currentProfile != null', this.state.userAccount.currentProfile != null)
            return this.state.userAccount.currentProfile != null;
        },

        /*hasAuth(context) {
            const hasAuth = clientApiService.hasAuth()

            let user = {

            }

            if (hasAuth) {
                console.log('hasAuth')
                this.authUser(user)
            }
            else {
                console.log('NO AUTH')
            }
        },*/


        async doLogout(context) {
            context.commit('clearUser')
            window.location.href = 'https://localhost:8080/logout'
        },

        loadInstDataToStorage(context, instFollowers) {
            context.commit('setInstFollowers', instFollowers)
        },
        loadInstUserProfile(context, instProfile) {
            context.commit('setInstProfile', instProfile)
            axios.post('/api/social/instagram/graph/save_profile', instProfile).then(value => {
                console.log(value)
            })
        },
        doGoogleAuth() {
           /*window.open('http://localhost:8080/login', '_blank');*/
            window.location.href = 'https://localhost:8080/login'
        }
    },
    plugins: [createPersistedState()]
});