<template>
    <v-container class="grey lighten-5" fluid>
    <v-row no-gutters>
        <v-col v-if="this.showEmail" :cols="9">
            <EmailPane :email="this.results[this.idx]" :closeEmail="this.showSearch"/>
        </v-col>
        <v-col v-else :cols="9">
            <v-row no-gutters>
                <v-row dense no-gutters><h1>{{this.project.name}}</h1></v-row>
            </v-row>
            <v-row dense no-gutters>
                <h5 class="status-header">status: {{this.project.status}}</h5>
            </v-row>
            <v-row>
                <br/>
            </v-row>
            <v-row>
                <v-divider/>
            </v-row>
            <v-row>
                <br/>
            </v-row>
            <v-row  v-for="(result, i) in this.results" :key="i" dense>
                <v-card outlined class="mx-auto" max-width="1000" :key="i" @click="showCurrentEmail(i)">
                    <div class="d-flex">
                        <v-avatar class="ma-2" size="150" tile>
                            <v-img src="@/assets/email.svg"></v-img>
                        </v-avatar>
                        <v-row>
                            <div class="overline ma-2">tag: {{result.tag}}</div>
                            <v-card-title class="headline">{{result.name}}</v-card-title>
                            <v-card-subtitle>author: {{result.author}}</v-card-subtitle>
                        </v-row>
                        <v-card-text class="text-truncate">{{result.emailData.body}}</v-card-text>
                        <v-avatar class="ma-2 progress" tile size="100">
                            <v-progress-circular color="blue lighten-2" :value="Math.floor(result.score) + 40">
                                {{Math.floor(result.score) + 40}}
                            </v-progress-circular>
                        </v-avatar>
                    </div>
                </v-card>
            </v-row>
        </v-col>
        <v-col :cols="3" class="menu-column" align="center">
            <v-container class="menu-container">
                <h1 class="menu-header" v-text="this.project.name"/>
                <br/>
                <br/>
                <v-divider/>
                <v-container>
                    <p class="menu-text title">Current Documents</p>
                    <p class="menu-text title">1 - 10</p>
                    <p/>
                </v-container>
                <v-divider/>
                <v-container>
                    <p/>
                    <div v-if="this.showEmail">
                        <v-btn large min-width="245" @click="this.showSearch">Close Preview</v-btn>
                    </div>
                    <div v-else>
                        <v-btn large min-width="245" disabled>Close Preview</v-btn>
                    </div>
                </v-container>
            </v-container>
        </v-col>
    </v-row>
    <!-- old page -->
    </v-container>
</template>

<script>
    import EmailPane from '@/components/project/EmailPane'
    import {StitchServices} from '@/plugins/StitchPlugin'

    export default {
        name: 'ProjectPage',
        components: {
            EmailPane
        },
        props: [
            'caseid',
            'projectid'
        ],
        data () {
            return {
                pageLoading: true,
                showEmail: false,
                project: {},
                results: [],
                idx: -1
            }
        },
        created() {
            this.getProject()
            this.pageLoading = false
        },
        methods: {
            showSearch(){
                this.showEmail = false
            },
            showCurrentEmail(k){
                this.idx = k
                this.showEmail = true
            },
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
                    let i = 0;
                    let j = 0;
                    /*
                     * tag emails which were used during training
                     */
                    for(i = 0; i < result.result.length; i++){
                        result.result[i].tag = 'none'
                        let t = '' + result.result[i]._id
                        for(j = 0; j < this.project.interestingIds.length; j++){
                            let s = '' + this.project.interestingIds[j]
                            if(t === s){
                                result.result[i].tag = 'interesting'
                            }
                        }

                        for(j = 0; j < this.project.uninterestingIds.length; j++){
                            let s = '' + this.project.uninterestingIds[j]
                            if(t === s){
                                result.result[i].tag = 'uninteresting'
                            }
                        }
                    }
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
    .status-header {
        padding-bottom: 17px;
    }
</style>