<template>
    <v-container class="grey lighten-5" fluid>
        <div v-if="project === {}">
            <v-col align="center" cols="12">
                <pulse-loader/>
            </v-col>
        </div>
        <v-row v-else>
            <v-col :cols="9">
                <v-row dense no-gutters>
                    <h1>{{this.project.name}}</h1>
                </v-row>
                <v-row dense no-gutters>
                    <h5>status: {{this.project.status}}</h5>
                </v-row>
                <v-row>
                    <br/>
                </v-row>
                <v-row>
                    <v-divider/>
                </v-row>
                <v-row v-for="(result, i) in this.results" :key="i" dense>
                    <v-card outlined class="mx-auto" max-width="1000" @click.stop="openEmail = true">
                            <div class="d-flex">
                                <v-avatar class="ma-2" size="100" tile>
                                    <v-img src="@/assets/email.svg"></v-img>
                                </v-avatar>
                                <v-row>
                                <v-card-title class="overflow-auto headline">{{result.name}}</v-card-title>
                                <v-card-subtitle>{{result.author}}</v-card-subtitle>
                                </v-row>
                                <v-card-text class="text-truncate">{{result.emailData.body}}</v-card-text>
                                <v-avatar class="ma-2 progress" tile size="100">
                                    <v-progress-circular color="blue lighten-2" :value="Math.floor(result.score) + 40">
                                        {{Math.floor(result.score) + 40}}
                                    </v-progress-circular>
                                </v-avatar>
                            </div>
                    </v-card>
                    <v-dialog transition="fade-transition" light max-width="900px" align="center" v-model="openEmail">
                        <v-card flat class="result-card">
                            <v-card-actions>
                                <v-card-title class="result-card-title">Email Subject:<span class="span-title"> {{result.name || ''}}</span></v-card-title>
                                <v-btn icon absolute top right @click="openEmail = false"><v-icon>clear</v-icon></v-btn>
                            </v-card-actions>
                            <v-divider/>
                            <v-card-text>
                                <v-row>
                                    <v-col cols="3">
                                        <v-row>
                                            <v-list dense max-height="250" class="overflow-y-auto">
                                                <v-subheader class="result-title">Key Phrases</v-subheader>
                                                <v-list-item class="result-list" dense v-for="(kp, i) in result.analysis.comprehend.keyPhrases" :key="i">
                                                    <v-list-item-content>
                                                        <v-list-item-title align="left">{{kp.text}}</v-list-item-title>
                                                    </v-list-item-content>
                                                    <v-list-item-content>
                                                        <v-list-item-subtitle align="right">{{kp.score.toFixed(2)}}</v-list-item-subtitle>
                                                    </v-list-item-content>
                                                </v-list-item>
                                            </v-list>
                                        </v-row>
                                        <v-row>
                                            <v-divider/>
                                        </v-row>
                                        <v-row>
                                            <v-list dense max-height="250" class="overflow-y-auto">
                                                <v-subheader class="result-title">Entities</v-subheader>
                                                <v-list-item class="result-list" dense v-for="(e, i) in result.analysis.comprehend.entities" :key="i">
                                                    <v-list-item-content>
                                                        <v-list-item-title align="left">{{e.text}}</v-list-item-title>
                                                    </v-list-item-content>
                                                    <v-list-item-content>
                                                        <v-list-item-title align="left">({{e.type}})</v-list-item-title>
                                                    </v-list-item-content>
                                                    <v-list-item-content>
                                                        <v-list-item-subtitle align="right">{{e.score.toFixed(2)}}</v-list-item-subtitle>
                                                    </v-list-item-content>
                                                </v-list-item>
                                            </v-list>
                                        </v-row>
                                    </v-col>
                                    <v-col cols="1">
                                        <v-divider vertical/>
                                    </v-col>
                                    <v-col cols="8" class="email-container">
                                        <v-row class="email-row" dense no-gutters>
                                            <v-col cols="2"><h3 align="left">Author</h3></v-col>
                                            <v-col><p align="left">{{result.author}}</p> </v-col>
                                        </v-row>
                                        <v-row class="email-row" dense no-gutters>
                                            <v-col cols="2"><h3 align="left">From</h3></v-col>
                                            <v-col><p align="left">{{result.from}}</p></v-col>
                                        </v-row>
                                        <v-row class="email-row" dense no-gutters>
                                            <v-col cols="2"><h3 align="left">To</h3></v-col>
                                            <v-col v-if="result.emailData !== undefined">
                                                <p align="left" v-for="t in result.emailData.to.slice(0,2)" :key="t">{{t}}</p>
                                            </v-col>
                                        </v-row>
                                        <v-row class="email-row" dense no-gutters>
                                            <v-col cols="2"><h3 align="left">Subject</h3></v-col>
                                            <v-col v-if="result.emailData !== undefined">
                                                <p align="left">{{result.emailData.subject}}</p>
                                            </v-col>
                                        </v-row>
                                        <v-row class="email-row" dense no-gutters>
                                            <v-col cols="2"><h3 align="left">Message-Id</h3></v-col>
                                            <v-col v-if="result.content !== undefined">
                                                <p align="left">{{`${result.content.split('\r\n')[0].split(':')[1]}`}}</p>
                                            </v-col>
                                        </v-row>
                                        <v-row class="email-row" dense no-gutters>
                                            <v-col cols="2"><h3 align="left">Body</h3></v-col>
                                            <v-col v-if="result.emailData !== undefined">
                                                <v-textarea filled :value="result.emailData.body"/>
                                            </v-col>
                                        </v-row>
                                    </v-col>
                                </v-row>
                            </v-card-text>
                        </v-card>
                    </v-dialog>
                </v-row>
            </v-col>
            <v-col :cols="3" class="menu-column" align="center">
                <v-container class="menu-container">
                    <h1 class="menu-header" v-text="this.project.name || 'default'"/>
                    <v-divider/>
                    <v-container>
                        <p class="menu-text title">Matching Documents</p>
                        <p class="menu-text title">{{this.project.documentsProcessed}}</p>
                        <p/>
                    </v-container>
                    <v-divider/>
                    <v-container>
                        <p/>
                        <v-btn large min-width="245" disabled>Preview</v-btn>
                    </v-container>
                </v-container>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import {StitchServices} from '@/plugins/StitchPlugin'
    import PulseLoader from 'vue-spinner/src/PulseLoader'

    export default {
        name: 'ProjectPage',
        props: [
            'caseid',
            'projectid'
        ],
        components: {
            PulseLoader
        },
        data () {
            return {
                pageLoading: true,
                openEmail: false,
                project: {},
                results: []
            }
        },
        created() {
            this.getProject()
            this.pageLoading = false
        },
        methods: {
            getProject(){
                const projectsCollection = StitchServices.getProjectsCollection()
                projectsCollection.findOne({_id: StitchServices.getObjectId(this.projectid)})
                    .then(doc => {
                        this.project = doc
                        this.runSearch(this.project.searchTerms)
                        this.pageLoading = false
                    })
            },
            runSearch(terms) {
                StitchServices.client.callFunction(
                    'fts',
                    [{
                        q: terms,
                        p: [
                            'analysis.comprehend.keyPhrases.text',
                            'analysis.comprehend.entities.text',
                            'analysis.comprehend.entities.type',
                            'content'
                        ],
                        l: 10,
                        project: false
                    }]
                ).then(result => {
                    this.results = result.result
                })
            }
        }
    }
</script>

<style scoped lang="less">
 .progress {
     padding-top: 15px;
     align-content: flex-end;
 }
</style>