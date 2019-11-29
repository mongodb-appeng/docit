<template>
    <v-app-bar flat absolute app>
        <v-toolbar-title>
            <a class="brand" v-on:click="gotoHome"><v-img src="@/assets/logo.svg" alt="docit"/></a>
        </v-toolbar-title>
        <v-spacer/>
        <div v-if="userLoggedIn">
                <v-btn v-if="this.profile !== null" :to="this.profile.homepage" text>Research Project</v-btn>
            <v-menu offset-y>
                <template v-slot:activator="{ on }">
                    <v-btn icon v-on="on">
                        <v-icon large>account_circle</v-icon>
                    </v-btn>
                </template>
                <v-list>
                    <v-list-item @click="gotoProfile"><v-list-item-title>Profile</v-list-item-title></v-list-item>
                    <v-list-item @click="logout"><v-list-item-title>Logout</v-list-item-title></v-list-item>
                </v-list>
            </v-menu>
            </div>

    </v-app-bar>
</template>

<script>
    import { mapState } from 'vuex'

    export default {
        name: 'AppNav',
        data() {
            return {
                showBurgerNav: false
            }
        },
        computed: {
            ...mapState({
                userLoggedIn: state => state.profile.userLoggedIn,
                profile: state => state.profile.profile
            })
        },
        methods: {
            gotoHome() {
                this.$router.push(this.profile.homepage)
            },
            gotoProfile() {
                this.$router.push(`/profile/${this.profile.id}`)
            },
            logout() {
                this.$store.dispatch('profile/logout')
                    .then(() => {this.$router.push('/login')})
            }
        }
    }
</script>

<style scoped lang="less">
    .brand {
        padding-top: 10px;
    }
</style>