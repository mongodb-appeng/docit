import {
    ADD_PROJECT_INTERESTING_ID,
    ADD_PROJECT_SEARCH_TERM,
    ADD_PROJECT_UNINTERESTING_ID,
    CLEAR_PROJECT_DATA,
    INCREMENT_RESULT_IDX,
    REMOVE_PROJECT_INTERESTING_ID,
    REMOVE_PROJECT_SEARCH_TERM,
    REMOVE_PROJECT_UNINTERESTING_ID,
    RESET_PROJECT_INTERESTING_ID,
    RESET_PROJECT_SEARCH_TERM,
    RESET_PROJECT_UNINTERESTING_ID,
    TOGGLE_PROJECT_INSPECTION,
    SET_PROJECT_RESULTS,
    UPDATE_CURRENT_RESULT
} from '@/store/modules/projects-mutation-types'
import {StitchServices} from '@/plugins/StitchPlugin'

/*
 * each project page consists of a few components,
 * these components will use shared state for
 * data manipulations
 *
 * the current project we are working with
 * This will either be loaded from the database
 * or created from the page
 */
const state = {
    currentProject: null,
    searchTerms: [],
    interestingIds: [],
    uninterestingIds: [],
    inspectData: false,
    searchResultIds: [],
    currentResult: null,
    searchResultIdx: -1
}

const getters = {}

const actions = {
    clearProject({commit}){
        commit(CLEAR_PROJECT_DATA)
    },
    incrementResultIdx({commit}){
        commit(INCREMENT_RESULT_IDX)
    },
    setSearchResults({commit}, payload){
        commit(SET_PROJECT_RESULTS, payload)
    },
    toggleInspection({commit}){
        commit(TOGGLE_PROJECT_INSPECTION)
    },
    updateTerms({commit}, payload){
        switch(payload.action){
            case 'ADD':
                commit(ADD_PROJECT_SEARCH_TERM, payload)
                break
            case 'REMOVE':
                commit(REMOVE_PROJECT_SEARCH_TERM, payload)
                break
            case 'RESET':
                commit(RESET_PROJECT_SEARCH_TERM)
                break
        }
    },
    updateInterestingIds({commit}, payload){
        switch (payload.action) {
            case 'ADD':
                commit(ADD_PROJECT_INTERESTING_ID, payload)
                break
            case 'REMOVE':
                commit(REMOVE_PROJECT_INTERESTING_ID, payload)
                break
            case 'RESET':
                commit(RESET_PROJECT_INTERESTING_ID)
                break
        }
    },
    updateUninterestingIds({commit}, payload){
        switch (payload.action) {
            case 'ADD':
                commit(ADD_PROJECT_UNINTERESTING_ID, payload)
                break
            case 'REMOVE':
                commit(REMOVE_PROJECT_UNINTERESTING_ID, payload)
                break
            case 'RESET':
                commit(RESET_PROJECT_UNINTERESTING_ID)
                break
        }
    },
    updateCurrentResult({commit, state}){
        StitchServices.findOneEmail(state.searchResultIds[state.searchResultIdx])
            .then(doc => {
                commit(UPDATE_CURRENT_RESULT, {result: doc})
            })

    }
}

const mutations = {
    [ADD_PROJECT_INTERESTING_ID](state, payload) {
        const id = payload.id
        if (state.interestingIds.indexOf(id) === -1) {
            state.interestingIds.push(id)
        }
    },
    [ADD_PROJECT_SEARCH_TERM](state, payload){
        const term = payload.term
        if(state.searchTerms.indexOf(term) === -1){
            state.searchTerms.push(term)
        }
    },
    [ADD_PROJECT_UNINTERESTING_ID](state, payload) {
        const id = payload.id
        if (state.uninterestingIds.indexOf(id) === -1) {
            state.uninterestingIds.push(id)
        }
    },
    [CLEAR_PROJECT_DATA](state){
        state.currentProject = null
        state.searchTerms = []
        state.interestingIds = []
        state.uninterestingIds = []
        state.inspectData = false
        state.searchResultIds = []
        state.searchResultIdx = -1
        state.currentResult = null
    },
    [INCREMENT_RESULT_IDX](state){
        /*
         * TODO: need to sort out pagination here
         */
        if(state.searchResultIdx + 1 < state.searchResultIds.length){
            state.searchResultIdx += 1
        }
    },
    [REMOVE_PROJECT_INTERESTING_ID](state, payload){
        const id = payload.id
        const idx = state.interestingIds.indexOf(id)
        if(idx !== -1){
            state.interestingIds.splice(idx, 1)
        }
    },
    [REMOVE_PROJECT_SEARCH_TERM](state, payload){
        const term = payload.term
        const idx = state.searchTerms.indexOf(term)
        if(idx !== -1){
            state.searchTerms.splice(idx, 1)
        }
    },
    [REMOVE_PROJECT_UNINTERESTING_ID](state, payload){
        const id = payload.id
        const idx = state.uninterestingIds.indexOf(id)
        if(idx !== -1){
            state.uninterestingIds.splice(idx, 1)
        }
    },
    [RESET_PROJECT_INTERESTING_ID](state){
        state.interestingIds = []
    },
    [RESET_PROJECT_SEARCH_TERM](state){
        state.searchTerms = []
    },
    [RESET_PROJECT_UNINTERESTING_ID](state){
        state.uninterestingIds = []
    },
    [SET_PROJECT_RESULTS](state, payload){
        state.searchResultIds = payload.results
        state.searchResultIdx = 0
    },
    [TOGGLE_PROJECT_INSPECTION](state){
        state.inspectData = !state.inspectData
    },
    [UPDATE_CURRENT_RESULT](state, payload){
        state.currentResult = payload.result
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}