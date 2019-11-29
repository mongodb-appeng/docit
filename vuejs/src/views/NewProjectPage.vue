<template>
    <v-container class="grey lighten-5" fluid>
        <v-row no-gutters>
                <v-col v-if="this.inspectData" :cols="9">
                    <InspectData/>
                </v-col>
            <v-col v-else :cols="9">
                <v-row no-gutters>
                    <h1><input v-model="projectName" placeholder="enter project name"/></h1>
                </v-row>
                <v-row>
                    <v-col v-for="term in this.searchTerms" :key="term">
                        <v-btn min-width="100" outlined x-small @click="removeTerm(term)" color="#13AA52">
                            {{term}} <v-icon x-small dense>clear</v-icon>
                        </v-btn>
                    </v-col>
                </v-row>
                <v-row no-gutters>
                    <v-col>
                        <TaskSearch searchType="KEY_PHRASES" :caseid="caseid"/>
                    </v-col>
                    <v-col>
                        <TaskSearch searchType="ENTITIES" :caseid="caseid"/>
                    </v-col>
                    <v-col>
                        <TaskSearch searchType="TEXT_SEARCH" :caseid="caseid"/>
                    </v-col>
                </v-row>
            </v-col>
            <v-col :cols="3" class="menu-column" align="center">
                <v-container class="menu-container">
                    <h1 class="menu-header" v-text="projectName || 'default'"/>
                    <v-divider/>
                    <v-container>
                        <p class="menu-text title">Matching Documents</p>
                        <p class="menu-text title">{{this.totalDocs}}</p>
                        <p/>
                        <div v-if="this.inspectData">
                            <v-btn large outlined color="#13AA52" min-width="245" @click="toggleSearch">Filter Documents</v-btn>
                        </div>
                        <div v-else>
                            <div v-if="this.searchTerms.length > 0">
                                <v-btn class="inspect-btn" large depressed color="#13AA52" min-width="245" @click="runSearch">Inspect Documents</v-btn>
                            </div>
                            <div v-else>
                                <v-btn large disabled color="#13AA52" min-width="245">Inspect Documents</v-btn>
                            </div>
                        </div>
                        <p/>
                        <v-row>
                            <v-col>
                                <p class="menu-text subtitle-2">Interesting</p>
                                <p class="menu-text title">{{interesting}}</p>
                            </v-col>
                            <v-col>
                                <p class="menu-text subtitle-2">Uninteresting</p>
                                <p class="menu-text title">{{uninteresting}}</p>
                            </v-col>
                        </v-row>
                    </v-container>
                    <v-divider/>
                    <v-container>
                        <p/>
                        <div v-if="this.interesting > 0 && this.uninteresting > 0">
                            <v-btn large color="#13AA52" depressed min-width="245" @click="setupClassifier">Launch</v-btn>
                        </div>
                        <div v-else>
                            <v-btn large color="#13AA52"  min-width="245" disabled>Launch</v-btn>
                        </div>
                    </v-container>
                </v-container>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import TaskSearch from '@/components/project/TaskSearch'
    import InspectData from '@/components/project/InspectData'
    import {StitchServices} from '@/plugins/StitchPlugin'

    import {mapState} from 'vuex'

    export default {
        name: 'NewProjectPage',
        props: ['caseid'],
        data() {
            return {
                projectName: '',
                filter: true,
                totalDocs: 0
            }
        },
        computed: {
            ...mapState({
                searchTerms: state => state.projects.searchTerms,
                inspectData: state => state.projects.inspectData,
                interesting: state => state.projects.interestingIds.length,
                uninteresting: state => state.projects.uninterestingIds.length,
                interestingIds: state => state.projects.interestingIds,
                uninterestingIds: state => state.projects.uninterestingIds,
                profile: state => state.profile.profile
            }),
            canInspect() {
                return (this.searchTerms.length > 0)
            }
        },
        components: {
            TaskSearch,
            InspectData
        },
        methods: {
            removeTerm(term){
                this.$store.dispatch('projects/updateTerms', {action: 'REMOVE', term: term})
            },
            /*
             * TODO: need to sort out how to handle more than one case
             *  $searchBeta must be first
             */
            runSearch(){
                StitchServices.client.callFunction(
                    'fts',
                    [{
                        q: this.searchTerms,
                        p: [
                            'analysis.comprehend.keyPhrases.text',
                            'analysis.comprehend.entities.text',
                            'analysis.comprehend.entities.type',
                            'content'
                        ],
                        l: 50,
                        project: true
                    }]
                ).then(result => {
                    this.$store.dispatch('projects/setSearchResults', {results: result.result})
                    this.totalDocs = Math.floor(Math.random() * (500000 - 200000)) + 200000
                    this.$store.dispatch('projects/updateCurrentResult')
                    this.$store.dispatch('projects/toggleInspection')
                })
            },
            toggleSearch(){
                this.$store.dispatch('projects/toggleInspection')
            },
            setupClassifier(){
                /* eslint-disable no-console */
                const coll = StitchServices.getProjectsCollection()
                let name = 'default'
                if(this.projectName !== ''){
                    name = this.projectName
                }
                coll.insertOne({
                    name: name,
                    classifier: {},
                    caseId: StitchServices.getObjectId(this.caseid),
                    createdAt: new Date(),
                    searchTerms: this.searchTerms,
                    interestingIds: this.interestingIds,
                    uninterestingIds: this.uninterestingIds,
                    documentsProcessed: 0,
                    precentComplete: 0,
                    status: 'created project',
                    uid: StitchServices.client.auth.user.id
                }).then(() => {
                    this.$router.push(this.profile.homepage)
                })
            }
        },
        beforeDestroy() {
            this.$store.dispatch('projects/clearProject')
        }
    }
</script>

<style scoped>
    .menu-column {
        background-color: rgba(231,238,236,0.5);
        padding: 0;
    }

    .menu-header {
        text-align: left;
    }

    .menu-text {
        margin-bottom: 0;
        text-align: left;
    }
    .inspect-btn {
        color: white;
    }
</style>