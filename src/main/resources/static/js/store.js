import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
import router from './router'

export default new Vuex.Store({
    state: {
        currentUser: null,
        instProfile: null,
        newFollowersData: null
    },
    mutations: {
        addNewFollowersData(currentState, newFollowersData) {
            currentState.newFollowersData = newFollowersData
        },

        setInstProfile(currentState, profile) {
            currentState.instProfile = profile
        },
        setCurrentUser(currentState, user) {
            currentState.currentUser = user
        },
        logoutUser(currentState) {
            currentState.currentUser = null
        }
    },
    actions: {
        newFollowersData(context, user) {
            context.commit('addNewFollowersData', user)
        },

        loadInstUserProfile(context, user) {
            context.commit('setInstProfile', user)
        },
        doGoogleAuth() {
            ///window.open('http://localhost:8080/login', '_blank');
            window.location.href = 'https://localhost:8080/login'
        },
        async doLogout(context) {
            await axios.post('https://localhost:8080/logout')
            context.commit('logoutUser')
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