<template>
    <v-app id="my-base-app">


      <app-template></app-template>



        <!--<router-view></router-view>-->

    </v-app>
</template>

<script>
    import AppTemplate from "components/AppTemplate.vue";

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



        }
      },
      computed: {

      }
    }
</script>
