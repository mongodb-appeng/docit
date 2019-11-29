/* eslint-disable no-console */
/*
 * TODO: https://medium.com/@dillonchanis/handling-errors-in-vue-with-error-boundaries-91f6ead0093b
 *       need to understand our error bounds
 */
import Vue from 'vue'
import Vuex from 'vuex'
import createLogger from 'vuex/dist/logger'
import createPersistedState from 'vuex-persistedstate'
import cases from '@/store/modules/cases'
import profile from '@/store/modules/profile'
import projects from '@/store/modules/projects'
import status from '@/store/modules/status'

Vue.use(Vuex)

const debug = process.env.NODE_ENV !== 'production'
const plugins = [createPersistedState()]

if(debug){
    plugins.push(createLogger())
}

export default new Vuex.Store({
    strict: debug,
    plugins: plugins,
    modules: {
        cases,
        profile,
        projects,
        status
    }
})