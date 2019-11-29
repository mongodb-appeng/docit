import {CLEAR_CASE_DATA, SET_CASE_DATA} from '@/store/modules/cases-mutation-types'

const state = {
    currentCase: null
}

const getters = {}

const actions = {
    updateCurrentCase({commit}, payload){
        commit(SET_CASE_DATA, payload)
    }
}

const mutations = {
    [CLEAR_CASE_DATA](state){
        state.currentCase = null
    },
    [SET_CASE_DATA](state, payload){
        state.currentCase = payload.currentCase
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}