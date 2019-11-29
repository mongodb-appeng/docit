import Vue from 'vue'
import Router from 'vue-router'
import LoginPage from '@/views/LoginPage'
import CasePage from '@/views/CasePage'
import ProfilePage from '@/views/ProfilePage'
import ProjectPage from '@/views/ProjectPage'
import NewProjectPage from '@/views/NewProjectPage'
import store from '@/store'

Vue.use(Router)

/*
 * simple check to ensure user is authenticated
 */
const authEnter = (to, from, next) => {
    const authRequred = to.matched.some(route => route.meta.auth)
    const authed = store.state.profile.userLoggedIn
    if(authRequred && !authed){
        next('/login')
    } else {
        next()
    }
}

const homeEnter = (to, from, next) => {
    const authed = store.state.userLoggedIn
    if (authed) {
        next(store.state.profile.homepage)
    } else {
        next('/login')
    }
}

const router = new Router({
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            beforeEnter: homeEnter
        },
        {
            path: '/login',
            name: 'login',
            component: LoginPage,
            props: true,
            meta: {auth: false},
            beforeEnter: (to, from, next) =>{
                const authed = store.state.userLoggedIn
                if(authed) {
                    store.dispatch('userLogout', {})
                }
                next()
            }
        },
        {
            path: '/profile/:id',
            name: 'profile',
            component: ProfilePage,
            props: true,
            meta: {auth: true},
            beforeEnter: authEnter
        },
        {
            path: '/cases/:caseid',
            name: 'case',
            props: true,
            component: CasePage,
            meta: {auth: true},
            beforeEnter: authEnter
        },
        {
            path: '/cases/:caseid/projects/:projectid',
            name: 'project',
            component: ProjectPage,
            props: true,
            meta: {auth: true},
            beforeEnter: authEnter
        },
        {
            path: '/cases/:caseid/projects/',
            name: 'createProject',
            component: NewProjectPage,
            props: true,
            meta: {auth: true},
            beforeEnter: authEnter
        },
        {
            /* silly catch all right now */
            path: '*',
            beforeEnter: homeEnter

        }
    ]
})

router.onError(e => {
    store.dispatch('setErrorMessage', {msg: `a router error has occorred(${e})`})
})

export default router