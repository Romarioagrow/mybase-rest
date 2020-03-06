import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)
import router from './router'

export default new Vuex.Store({

    state: {
        currentUser: null
    },
    mutations: {
        setCurrentUser(currentState, user) {
            currentState.currentUser = user
        },
        logoutUser(currentState) {
            currentState.currentUser = null
        }
    },
    actions: {
        doGoogleAuth() {
            //window.open('http://localhost:8080/login', '_blank');
            window.location.href = 'https://localhost:8080/login'
        },
        async doLogout(context) {
            await axios.post('https://localhost:8080/logout')
            context.commit('logoutUser')
        },
        loadUser(context, user) {
            context.commit('setCurrentUser', user)
        },
        userAuthorized() {
        }
    }
});