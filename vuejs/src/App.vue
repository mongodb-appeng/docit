<template>
  <v-app app id="app">
    <AppNav/>
    <v-content>
      <router-view/>
    </v-content>
    <AppFooter/>
  </v-app>
</template>

<script>
  import Vue from 'vue'
  import Vuex from 'vuex'
  import config from '@/config'
  import AppNav from '@/components/AppNav'
  import AppFooter from '@/components/AppFooter'

  Vue.use(Vuex)

  export default {
    name: config.appName,
    components: {
      AppNav,
      AppFooter
    }
  }
</script>

<style>
</style>