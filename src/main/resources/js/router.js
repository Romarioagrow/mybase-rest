import Vue from 'vue';
import VueRouter from "vue-router";
import Home from "pages/Home.vue";
import User from "pages/User.vue";
import AuthPage from "pages/AuthPage.vue";
import Posts from "pages/Posts.vue";
import Instagram from "pages/Instagram.vue";
import Spending from "pages/Spending.vue";
import TheLogs from "pages/TheLogs.vue";
import GoalsPage from "pages/GoalsPage.vue";
import MyBase from "pages/MyBase.vue";
import OAuth2Redirect from "pages/OAuth2Redirect.vue";

Vue.use(VueRouter);
const routes = [
    {path: '/', component: Home},
    {path: '/profile', component: User},
    {path: '/auth', component: AuthPage},
    {path: '/posts', component: Posts},
    {path: '/instagram', component: Instagram},
    {path: '/spending', component: Spending},
    {path: '/thelogs', component: TheLogs},
    {path: '/goals', component: GoalsPage},
    {path: '/mybase', component: MyBase},
    {path: '/oauth2/redirect', component: OAuth2Redirect},
];

export default new VueRouter({
    mode: 'history',
    routes
})
