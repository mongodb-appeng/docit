import {CLEAR_CASE_DATA} from '@/store/modules/cases-mutation-types'
import {SET_USER_STATE, SET_PROFILE_STATE} from '@/store/modules/profile-mutation-types'
import {CLEAR_PROJECT_DATA} from '@/store/modules/projects-mutation-types'
import {CLEAR_ERROR_MESSAGE, SET_ERROR_MESSAGE} from '@/store/modules/status-mutation-types'
import {StitchServices} from '@/plugins/StitchPlugin'
import router from '@/router'

/*
 * the user profile consists of:
 * - stitch user object
 * - user profile from database
 */
const state = {
    userLoggedIn: false,
    user: null,
    profile: null
}

const getters = {}

const actions = {
    /*
     * login user
     * payload {username, password}
     */
    login({commit}, payload) {
        const {username, password} = payload
        if(username === undefined || username === ''){
            commit(`status/${SET_ERROR_MESSAGE}`, {msg: 'username is not set'}, {root: true})
        } else if(password === undefined || username === ''){
            commit(`status/${SET_ERROR_MESSAGE}`, {msg: 'password is not set'}, {root: true})
        } else {
            // login and retrieve user profile
            const userCollection = StitchServices.getUserCollection()
            StitchServices.login(username, password)
                .then(authed => {
                    commit(SET_USER_STATE, {user: authed.profile, userLoggedIn: true})
                    // stitch rule only allows reading and writing users own data, within
                    // user collection only one document per user right now
                    userCollection.findOne().then(d => {
                        commit(
                            SET_PROFILE_STATE,
                            {
                                profile: {
                                    id: d.uid,
                                    homepage: d.homepage,
                                    permissions: d.permissions,
                                    roles: d.roles,
                                    recentSearches: d.recentSearches
                                }
                            }
                        )
                        commit(`status/${CLEAR_ERROR_MESSAGE}`, null, {root: true})
                        /*
                         * TODO: is there a better way?
                         */
                        router.push(d.homepage)
                    }).catch(error => {
                        commit(
                            `status/${SET_ERROR_MESSAGE}`,
                            {msg: `failed to retrieve user profile: ${error.message}`},
                            {root: true}
                        )
                    })
                })
                .catch(error => {
                    commit(
                        `status/${SET_ERROR_MESSAGE}`,
                        {msg: `failed to login: ${error.message}`},
                        {root: true}
                    )
                })
        }
    },
    logout({commit, state}) {
        if(state.user != null){
            StitchServices.logout()
                .then(() => {
                    commit(SET_PROFILE_STATE, {profile: null})
                    commit(SET_USER_STATE, {user: null, userLoggedIn: false})
                    commit(`projects/${CLEAR_PROJECT_DATA}`, null, {root: true})
                    commit(`cases/${CLEAR_CASE_DATA}`, null, {root: true})
                    commit(`status/${CLEAR_ERROR_MESSAGE}`, null, {root: true})
                })
                .catch(error => {
                    commit(SET_ERROR_MESSAGE, {msg: `logout failed: ${error.message}`})
                })
        }
    },
}

const mutations = {
    [SET_USER_STATE](state, payload) {
        state.user = payload.user
        state.userLoggedIn = payload.userLoggedIn
    },
    [SET_PROFILE_STATE](state, payload){
        state.profile = payload.profile
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}