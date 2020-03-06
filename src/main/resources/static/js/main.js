import Vue          from 'vue'
import VueResource  from 'vue-resource'
import router       from "./router";
import App          from "pages/App.vue"
import Vuetify      from "vuetify";
import BootstrapVue from 'bootstrap-vue'
import Vuesax       from 'vuesax'
import 'vuetify/dist/vuetify.min.css'
import 'vue-material-design-icons/styles.css';
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'vuesax/dist/vuesax.css'
import 'material-icons/iconfont/material-icons.css';
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import store from 'store'
import VueMask from 'v-mask'
//import facebookLogin from 'facebook-login-vuejs';
//import FB from 'fb';

Vue.use(VueResource);
Vue.use(Vuetify);
//Vue.use(BootstrapVue);
Vue.use(Vuesax);
Vue.use(VueMask);
//Vue.use(facebookLogin)
//Vue.use(FB)





new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    router,
    store,
    render: a => a(App)
});

