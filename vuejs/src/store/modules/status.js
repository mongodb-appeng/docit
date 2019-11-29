import {
    CLEAR_ERROR_MESSAGE,
    CLEAR_LOADING_STATE,
    SET_ERROR_MESSAGE,
    SET_LOADING_STATE
} from '@/store/modules/status-mutation-types'

const state = {
    isLoading: false,
    inError: false,
    errorMessage: null
}

const getters = {}

const actions = {}

const mutations = {
    [CLEAR_ERROR_MESSAGE](state){
        state.inError = false
        state.errorMessage = null
    },
    [CLEAR_LOADING_STATE](state){
        state.isLoading = false
    },
    [SET_ERROR_MESSAGE](state, payload){
        state.inError = true
        state.errorMessage = payload.msg
    },
    [SET_LOADING_STATE](state){
        state.isLoading = true
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}