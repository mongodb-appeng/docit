<template>
    <v-container fluid class="main-content">
        <div v-if="currentCase === null">
            <v-col align="center" cols="12"><pulse-loader/></v-col>
        </div>
        <div v-else>
            <v-row><v-col cols="12"><h1>{{this.currentCase.name}}</h1></v-col></v-row>
            <div v-if="chartsLoading">
                <v-row><v-col align="center" cols="12"><pulse-loader/></v-col></v-row>
            </div>
            <div v-else>
                <ChartsPane :chartUrls="this.chartUrls"/>
            </div>
            <v-divider/>
            <div>
                <h1>Research Projects</h1>
                <ProjectSelctionPane :projects="this.projects"/>
            </div>
        </div>
    </v-container>
</template>

<script>
    import ChartsPane from '@/components/project/ChartsPane'
    import ProjectSelctionPane from '@/components/project/ProjectSelctionPane'
    import {StitchServices} from '@/plugins/StitchPlugin'
    import PulseLoader from 'vue-spinner/src/PulseLoader'
    import {mapState, mapActions} from 'vuex'

    export default {
        name: 'CasePage',
        props: [
            'caseid'
        ],
        computed: {
            ...mapState({
                currentCase: state => state.cases.currentCase
            })
        },
        data () {
            return {
                chartUrls: [],
                pageLoading: true,
                chartsLoading: true,
                projects: [],
                projectsLoading: true
            }
        },
        methods: {
            ...mapActions(['updateCurrentCase']),
             getCase(){
                const caseColl = StitchServices.getCaseCollection()
                caseColl.findOne({_id: StitchServices.getObjectId(this.caseid)})
                    .then(doc => {
                        this.$store.dispatch('cases/updateCurrentCase', {currentCase: doc})
                        this.getProjects()
                        this.getChartUrls()
                    })
            },
            getProjects() {
                const projectsColl = StitchServices.getProjectsCollection()
                projectsColl.find({caseId: StitchServices.getObjectId(this.caseid)}).toArray()
                    .then(docs => {
                        this.projects = docs
                    })
            },
            async getChartUrls() {
                this.chartUrls = []
                for(let i = 0; i < this.currentCase.charts.length; i++){
                    let url = await StitchServices.client.callFunction(
                        'getChart',
                        [this.currentCase.charts[i], {'case.name': this.currentCase.name}, 86400, (3600*2)]
                    )
                    this.chartUrls.push(url)
                }
                this.chartsLoading = false
            }
        },
        created() {
            this.getCase()
            this.pageLoading = false
        },
        components: {
            PulseLoader,
            ChartsPane,
            ProjectSelctionPane
        }
    }
</script>

<style scoped lang="less">
    .container {
        height: 100em;
    }

    h1 {
        text-align: left;
        padding-left: 20px;
        padding-top: 20px;
    }
</style>