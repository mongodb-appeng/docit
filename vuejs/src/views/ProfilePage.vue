<template>
    <v-container fluid class="profile-content">
        <v-card class="mx-auto" outlined>
            <v-list-item three-line>
                <v-list-item-content>
                    <div class="overline mb-4">user profle</div>
                    <v-list-item-title class="headline mb-1">{{this.user.data.email}}</v-list-item-title>
                    <v-list-item-subtitle>{{this.profile.homepage}}</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-avatar tile size="80" color="white">
                    <v-icon x-large>account_circle</v-icon>
                </v-list-item-avatar>
            </v-list-item>
            <v-list-item>
                <v-list-item-content>
                    <v-list-item-title>permissions: {{this.profile.permissions}}</v-list-item-title>
                    <v-list-item-subtitle>roles: {{this.profile.roles.join(', ')}}</v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item>
                <v-list-item-content>
                    <div class="overline mb-4">Recent Searches</div>
                    <v-list-item-title v-for="(search, i) in this.profile.recentSearches" :key="i">
                        {{search.name}}
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </v-card>
    </v-container>
</template>

<script>
    import {mapState} from 'vuex'
    /*
     * TODO: update profile page
     */
    export default {
        name: 'ProfilePage',
        computed: {
            ...mapState({
                profile: state => state.profile.profile,
                user: state => state.profile.user
            })
        }
    }
</script>

<style scoped>

</style>