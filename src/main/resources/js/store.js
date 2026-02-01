import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

// Configure axios defaults
const API_BASE_URL = window.location.origin;

// Create axios instance with interceptors
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add request interceptor to include JWT token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle token refresh
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
                try {
                    const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
                        refreshToken: refreshToken
                    });

                    const { token, refreshToken: newRefreshToken } = response.data;

                    localStorage.setItem('accessToken', token);
                    localStorage.setItem('refreshToken', newRefreshToken);

                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    return api(originalRequest);
                } catch (refreshError) {
                    // Refresh failed, clear tokens and redirect to login
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    window.location.href = '/auth';
                    return Promise.reject(refreshError);
                }
            }
        }

        return Promise.reject(error);
    }
);

export default new Vuex.Store({
    state: {
        userAccount: {
            hasServerAuthorisation: false,
            currentProfile: null,
            instProfile: null,
            instFollowers: null
        },
        auth: {
            isAuthenticated: false,
            user: null,
            loading: false,
            error: null
        }
    },

    mutations: {
        setCurrentProfile(currentState, currentProfile) {
            currentState.userAccount.currentProfile = currentProfile
            currentState.userAccount.hasServerAuthorisation = true
        },

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
            currentState.auth.isAuthenticated = false
            currentState.auth.user = null
        },

        // OAuth2/JWT mutations
        AUTH_START(state) {
            state.auth.loading = true;
            state.auth.error = null;
        },

        AUTH_SUCCESS(state, user) {
            state.auth.isAuthenticated = true;
            state.auth.user = user;
            state.auth.loading = false;
            state.auth.error = null;
            state.userAccount.hasServerAuthorisation = true;
            state.userAccount.currentProfile = user;
        },

        AUTH_ERROR(state, error) {
            state.auth.isAuthenticated = false;
            state.auth.user = null;
            state.auth.loading = false;
            state.auth.error = error;
        },

        AUTH_LOGOUT(state) {
            state.auth.isAuthenticated = false;
            state.auth.user = null;
            state.auth.loading = false;
            state.auth.error = null;
            state.userAccount.hasServerAuthorisation = false;
            state.userAccount.currentProfile = null;
        }
    },

    getters: {
        userAuth: state =>
            state.userAccount
            && state.userAccount.currentProfile
            && state.userAccount.hasServerAuthorisation,

        isAuthenticated: state => state.auth.isAuthenticated,

        currentUser: state => state.auth.user,

        authLoading: state => state.auth.loading,

        authError: state => state.auth.error
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
        },

        isUserAuth() {
            console.log('this.state.currentProfile != null', this.state.userAccount.currentProfile != null)
            return this.state.userAccount.currentProfile != null;
        },

        // OAuth2 actions
        async handleOAuth2Callback({ commit, dispatch }, { token, refreshToken, error }) {
            if (error) {
                commit('AUTH_ERROR', decodeURIComponent(error));
                return { success: false, error: decodeURIComponent(error) };
            }

            if (token && refreshToken) {
                localStorage.setItem('accessToken', token);
                localStorage.setItem('refreshToken', refreshToken);

                try {
                    await dispatch('fetchCurrentUser');
                    return { success: true };
                } catch (err) {
                    commit('AUTH_ERROR', 'Failed to fetch user data');
                    return { success: false, error: 'Failed to fetch user data' };
                }
            }

            commit('AUTH_ERROR', 'No token received');
            return { success: false, error: 'No token received' };
        },

        async fetchCurrentUser({ commit }) {
            commit('AUTH_START');

            try {
                const response = await api.get('/auth/me');
                commit('AUTH_SUCCESS', response.data);
                return response.data;
            } catch (error) {
                const errorMessage = error.response?.data?.error || 'Failed to fetch user';
                commit('AUTH_ERROR', errorMessage);
                throw error;
            }
        },

        async checkAuthStatus({ commit, dispatch }) {
            const token = localStorage.getItem('accessToken');

            if (!token) {
                commit('AUTH_LOGOUT');
                return false;
            }

            try {
                await dispatch('fetchCurrentUser');
                return true;
            } catch (error) {
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                commit('AUTH_LOGOUT');
                return false;
            }
        },

        async doLogout({ commit }) {
            try {
                await api.post('/auth/logout');
            } catch (error) {
                console.error('Logout error:', error);
            }

            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            commit('AUTH_LOGOUT');
        },

        // OAuth2 provider redirects
        doGoogleAuth() {
            window.location.href = '/oauth2/authorize/google';
        },

        doFacebookAuth() {
            window.location.href = '/oauth2/authorize/facebook';
        },

        doInstagramAuth() {
            window.location.href = '/oauth2/authorize/instagram';
        },

        // Legacy actions (kept for compatibility)
        loadInstDataToStorage(context, instFollowers) {
            context.commit('setInstFollowers', instFollowers)
        },

        loadInstUserProfile(context, instProfile) {
            context.commit('setInstProfile', instProfile)
            api.post('/api/social/instagram/graph/save_profile', instProfile).then(value => {
                console.log(value)
            })
        }
    },

    plugins: [createPersistedState({
        paths: ['auth', 'userAccount']
    })]
});

// Export the api instance for use in components
export { api };
