<template>
    <v-card flat min-height="600" class="result-card">
        <v-card-title class="result-card-title">{{searchLabel}}</v-card-title>
        <v-card-text class="result-card-text">
            <v-text-field @keyup.enter.native="addTermEvent" v-model="eventData" outlined :label="searchLabel" prepend-inner-icon="search"/>
        </v-card-text>
        <v-list max-height="400" v-scroll dense subheader class="result-list">
            <div v-if="loading">
                <v-skeleton-loader class="mx-auto" max-width="300" max-height="500" type="card"/>
            </div>
            <div v-else>
                <v-list dense v-model="searchData">
                    <v-list-item dense class="result-list" v-for="(result, i) in results" :key="i" @click="addTerm(result.name)">
                        <v-list-item-content class="result-list-item-content subtitle-2" v-text="result.name"/>
                        <v-list-item-action><v-icon dense light class="result-icon result-list-item-content">add</v-icon></v-list-item-action>
                    </v-list-item>
                </v-list>
            </div>
        </v-list>
    </v-card>
</template>

<script>
    import {mapState} from 'vuex'
    import {StitchServices} from '@/plugins/StitchPlugin'

    export default {
        name: 'TaskSearch',
        props: ['searchType', 'caseid'],
        data() {
            return {
                loading: false,
                eventData: '',
                searchData: [],
                results: []
            }
        },
        computed: {
            ...mapState({
                profile: state => state.profile.profile,
                searchTerms: state => state.projects.searchTerms,
                currentCase: state => state.cases.currentCase
            }),
            searchLabel: function() {
                if(this.searchType === 'KEY_PHRASES') {
                    return 'Key Phrases'
                } else if(this.searchType === 'ENTITIES') {
                    return 'Entities'
                } else if(this.searchType === 'TEXT_SEARCH') {
                    return 'Text Search'
                } else {
                    /* we should never get here */
                    return 'Default Search'
                }
            }
        },
        created() {
            this.getSearchResults()
        },
        methods: {
            addTerm: function(term){
                this.$store.dispatch('projects/updateTerms', {action: 'ADD', term: term})
            },
            addTermEvent: function(event){
                event.preventDefault()
                this.$store.dispatch('projects/updateTerms', {action: 'ADD', term: this.eventData})
                this.eventData = ''
            },
            getSearchResults: function() {
                this.loading = true
                if(this.searchType === 'TEXT_SEARCH') {
                    this.loading = false
                    this.results = this.profile.recentSearches
                } else {
                    StitchServices.client.callFunction(
                        'getDataCounts',
                        [{
                            exclusionList: this.currentCase.exclusionList,
                            searchType: this.searchType,
                        }]
                    ).then(results => {
                        this.loading = false;
                        this.results = results.result
                    }).catch(() => {
                        /* todo: fill in error here */
                    })
                }
            }
        }
    }
</script>

<style scoped>
    .result-card {
        padding: 0;
    }

    .result-card-title {
        padding-top: 10px;
        padding-bottom: 0;
    }

    .result-card-text {
        padding-bottom: 0;
    }

    .result-list {
        padding-top: 0;
        padding-bottom: 0;
    }

    .result-list:hover {
        background: rgba(225,242,246,0.5);
    }
</style>