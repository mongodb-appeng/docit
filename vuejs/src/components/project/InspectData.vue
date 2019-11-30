<template>
    <v-container>
        <div v-if="this.searchResultIds.length === 0">
            <v-card>
                <v-card-actions>
                    <v-card-title class="result-card-title">No Emails in search</v-card-title>
                    <v-btn icon absolute top right @click="this.toggleInspection"><v-icon>clear</v-icon></v-btn>
                </v-card-actions>
                <v-divider/>
                <v-card-text>
                    <p>Please change search parameters</p>
                </v-card-text>
            </v-card>
        </div>
        <div v-else>
            <v-card v-if="this.currentResult !== null" flat min-height="600" class="result-card">
                <v-card-actions>
                    <v-card-title v-if="this.currentResult !== null" class="result-card-title">Email Subject:<span class="span-title"> {{this.currentResult.name || ''}}</span></v-card-title>
                    <v-btn icon absolute top right @click="this.toggleInspection"><v-icon>clear</v-icon></v-btn>
                </v-card-actions>
                <v-divider/>
                <v-card-text>
                    <v-row>
                        <v-col cols="3">
                            <v-row>
                                <AnalysisPane title="Key Phrases" :currentAnalysis="this.currentResult.analysis.comprehend.keyPhrases"/>
                            </v-row>
                            <v-row>
                                <v-divider/>
                            </v-row>
                            <v-row>
                                <AnalysisPane title="Entities" :currentAnalysis="this.currentResult.analysis.comprehend.entities"/>
                            </v-row>
                        </v-col>
                        <v-col cols="1">
                            <v-divider vertical/>
                        </v-col>
                        <v-col v-if="this.currentResult !== undefined" cols="8" class="email-container">
                            <v-row class="email-row" dense no-gutters>
                                <v-col cols="2"><h3 align="left">Author</h3></v-col>
                                <v-col><p align="left">{{this.currentResult.author}}</p> </v-col>
                            </v-row>
                            <v-row class="email-row" dense no-gutters>
                                <v-col cols="2"><h3 align="left">From</h3></v-col>
                                <v-col><p align="left">{{this.currentResult.from}}</p></v-col>
                            </v-row>
                            <v-row class="email-row" dense no-gutters>
                                <v-col cols="2"><h3 align="left">To</h3></v-col>
                                <v-col v-if="this.currentResult.emailData !== undefined">
                                    <p align="left" v-for="t in this.currentResult.emailData.to.slice(0,2)" :key="t">{{t}}</p>
                                </v-col>
                            </v-row>
                            <v-row class="email-row" dense no-gutters>
                                <v-col cols="2"><h3 align="left">Subject</h3></v-col>
                                <v-col v-if="this.currentResult.emailData !== undefined">
                                    <p align="left">{{this.currentResult.emailData.subject}}</p>
                                </v-col>
                            </v-row>
                            <v-row class="email-row" dense no-gutters>
                                <v-col cols="2"><h3 align="left">Message-Id</h3></v-col>
                                <v-col v-if="this.currentResult.content !== undefined">
                                    <p align="left">{{`${this.currentResult.content.split('\r\n')[0].split(':')[1]}`}}</p>
                                </v-col>
                            </v-row>
                            <v-row class="email-row" dense no-gutters>
                                <v-col cols="2"><h3 align="left">Body</h3></v-col>
                                <v-col v-if="this.currentResult.emailData !== undefined">
                                    <v-textarea filled :value="this.currentResult.emailData.body"/>
                                </v-col>
                            </v-row>

                            <v-divider/>
                            <v-row>
                                <v-col><v-btn @click="interesting" outlined min-width="250"><v-icon>check</v-icon>interesting</v-btn></v-col>
                                <v-col><v-btn @click="uninteresting" outlined min-width="250"><v-icon>clear</v-icon>uninteresting</v-btn></v-col>
                            </v-row>
                        </v-col>
                    </v-row>
                </v-card-text>
            </v-card>
            <v-card v-else>
                <v-card-actions>
                    <v-card-title class="result-card-title">Loading</v-card-title>
                    <v-btn icon absolute top right @click="this.toggleInspection"><v-icon>clear</v-icon></v-btn>
                </v-card-actions>
                <v-divider/>
                <v-card-text>
                    <v-row>
                        <v-col align="center"><pulse-loader/></v-col>
                    </v-row>
                </v-card-text>
            </v-card>
        </div>
    </v-container>
</template>

<script>
    import PulseLoader from 'vue-spinner/src/PulseLoader'
    import {mapActions, mapState} from 'vuex'
    import AnalysisPane from '@/components/project/AnalysisPane'

    export default {
        name: 'InspectData',
        data() {
            return {
                loading: true,
                searchData: []
            }
        },
        components: {
            AnalysisPane,
            PulseLoader
        },
        computed: {
            ...mapState({
                searchResultIds: state => state.projects.searchResultIds,
                searchResultIdx: state => state.projects.searchResultIdx,
                currentResult: state => state.projects.currentResult
            })
        },
        methods: {
            ...mapActions('projects', ['toggleInspection']),
            interesting: function(){
                this.$store.dispatch('projects/updateInterestingIds', {action: 'ADD', id: this.searchResultIds[this.searchResultIdx]._id})
                this.$store.dispatch('projects/incrementResultIdx')
                this.$store.dispatch('projects/updateCurrentResult')
            },
            uninteresting: function(){
                this.$store.dispatch('projects/updateUninterestingIds', {action: 'ADD', id: this.searchResultIds[this.searchResultIdx]._id})
                this.$store.dispatch('projects/incrementResultIdx')
                this.$store.dispatch('projects/updateCurrentResult')
            }
        }
    }
</script>

<style scoped>
    .result-card-title {
        font-weight: bold;
    }

    .span-title {
        font-weight: lighter;
        color: dimgrey;
    }
</style>