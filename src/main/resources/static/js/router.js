import Vue          from 'vue';
import VueRouter    from "vue-router";
import Home      from "pages/Home.vue";
import User      from "pages/User.vue";
import Login      from "pages/Login.vue";
import Posts      from "pages/Posts.vue";
import Instagram      from "pages/Instagram.vue";
import Spending      from "pages/Spending.vue";


Vue.use(VueRouter);
const routes = [
    {path: '/', component: Home},
    {path: '/profile', component: User},
    {path: '/cabinet', component: Login},
    {path: '/posts', component: Posts},
    {path: '/instagram', component: Instagram},
    {path: '/spending', component: Spending},
];

export default new VueRouter({
    mode: 'history',
    routes
})