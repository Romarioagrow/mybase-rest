<template>
    <v-app id="my-base-app">

      <app-template></app-template>

    </v-app>
</template>

<script>
    import AppTemplate from "components/AppTemplate.vue";

    //import ClientApiService from "services/ClientApiService.js"

    const clientApiService = require('services/ClientApiService.js');

    export default {
      components: {AppTemplate},
      data() {
        return {

        }
      },
      created() {
        this.initApp()
      },
      methods: {
        initApp() {
          console.log('App loaded')

          clientApiService.showInfo();



        }
      },
      computed: {

      }
    }
</script>
