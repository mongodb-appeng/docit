<template>
    <v-container>
        <v-card flat min-height="600" class="result-card">
            <v-card-actions>
                <v-card-title class="result-card-title">Email Subject:<span class="span-title"> {{this.email.name || ''}}</span></v-card-title>
                <br/>
                <v-card-subtitle>Score: {{Math.floor(email.score) + 40}}</v-card-subtitle>
                <v-btn icon absolute top right @click="this.closeEmail"><v-icon>clear</v-icon></v-btn>
            </v-card-actions>
            <v-divider/>
            <v-card-text>
                <v-row>
                    <v-col cols="3">
                        <v-row>
                            <EmailAnalysisPane title="Key Phrases" :currentAnalysis="this.email.analysis.comprehend.keyPhrases"/>
                        </v-row>
                        <v-row>
                            <v-divider/>
                        </v-row>
                        <v-row>
                            <EmailAnalysisPane title="Entities" :currentAnalysis="this.email.analysis.comprehend.entities"/>
                        </v-row>
                    </v-col>
                    <v-col cols="1">
                        <v-divider vertical/>
                    </v-col>
                    <v-col cols="8" class="email-container">
                        <v-row class="email-row" dense no-gutters>
                            <v-col cols="2"><h3 align="left">Author</h3></v-col>
                            <v-col><p align="left">{{this.email.author}}</p> </v-col>
                        </v-row>
                        <v-row class="email-row" dense no-gutters>
                            <v-col cols="2"><h3 align="left">From</h3></v-col>
                            <v-col><p align="left">{{this.email.from}}</p></v-col>
                        </v-row>
                        <v-row class="email-row" dense no-gutters>
                            <v-col cols="2"><h3 align="left">To</h3></v-col>
                            <v-col v-if="this.email.emailData !== undefined">
                                <p align="left" v-for="t in this.email.emailData.to.slice(0,2)" :key="t">{{t}}</p>
                            </v-col>
                        </v-row>
                        <v-row class="email-row" dense no-gutters>
                            <v-col cols="2"><h3 align="left">Subject</h3></v-col>
                            <v-col v-if="this.email.emailData !== undefined">
                                <p align="left">{{this.email.emailData.subject}}</p>
                            </v-col>
                        </v-row>
                        <v-row class="email-row" dense no-gutters>
                            <v-col cols="2"><h3 align="left">Message-Id</h3></v-col>
                            <v-col v-if="this.email.content !== undefined">
                                <p align="left">{{`${this.email.content.split('\r\n')[0].split(':')[1]}`}}</p>
                            </v-col>
                        </v-row>
                        <v-row class="email-row" dense no-gutters>
                            <v-col cols="2"><h3 align="left">Body</h3></v-col>
                            <v-col v-if="this.email.emailData !== undefined">
                                <v-textarea filled :value="this.email.emailData.body"/>
                            </v-col>
                        </v-row>
                        <v-divider/>
                    </v-col>
                </v-row>
            </v-card-text>
        </v-card>
    </v-container>
</template>

<script>
    import EmailAnalysisPane from '@/components/project/EmailAnalysisPane'

    export default {
        name: 'emailPane',
        components: {
            EmailAnalysisPane
        },
        props: [
            'email',
            'closeEmail'
        ]
    }
</script>

<style scoped>

</style>