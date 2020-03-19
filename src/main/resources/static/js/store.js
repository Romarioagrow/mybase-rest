import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
import router from './router'

export default new Vuex.Store({
    state: {
        /*currentUser: null,*/

        instProfile: null,
        instFollowers: null

        //newFollowersData: null,
    },
    mutations: {
        setInstFollowers(currentState, instFollowers) {
            currentState.instFollowers = instFollowers
        },

        /*addNewFollowersData(currentState, newFollowersData) {
            currentState.newFollowersData = newFollowersData
        },*/

        setInstProfile(currentState, profile) {
            currentState.instProfile = profile
        },
        setCurrentUser(currentState, user) {
            currentState.currentUser = user
        },
        logoutUser(currentState) {
            currentState.instProfile = null
            currentState.instFollowers = null
        }
    },
    actions: {
        async doLogout(context) {
            context.commit('logoutUser')
            //await axios.post('https://localhost:8080/logout')
        },


        loadInstDataToStorage(context, instFollowers) {
            context.commit('setInstFollowers', instFollowers)
        },

        /*newFollowersData(context, user) {
            context.commit('addNewFollowersData', user)
        },*/

        loadInstUserProfile(context, instProfile) {
            context.commit('setInstProfile', instProfile)
            axios.post('/api/social/instagram/graph/save_profile', instProfile).then(value => {
                console.log(value)
            })
        },
        doGoogleAuth() {
            ///window.open('http://localhost:8080/login', '_blank');
            window.location.href = 'https://localhost:8080/login'
        },


        loadUser(context, user) {
            console.log('USER LOADED: ' + user.toString())
            context.commit('setCurrentUser', user)
        },
        userAuthorized() {
        }
    },
    plugins: [createPersistedState()]
});