<template>
    <section class="login hero">
        <v-alert v-if="inError" type="error">{{msg}}</v-alert>
        <div class="hero-body">
            <div class="login-container">
                <img src="@/assets/logo-icon.svg"/>
                <h1 class="form-h1">Welcome to Docit.</h1>
                <p class="form-p">Sign in to Continue</p>
                <form id="login" @submit.prevent="doAuth">
                    <input class="form-input" v-model="username" type="text" placeholder="Username"/>
                    <br/>
                    <input class="form-input" v-model="password" type="password" placeholder="Password"/>
                    <br/>
                    <v-btn :disabled="disabled" block large color="#13AA52" @click="doAuth">Login</v-btn>
                </form>
            </div>
        </div>
    </section>
</template>

<script>
    import { mapState } from 'vuex'

    export default {
        name: 'LoginPage',
        computed: {
            ...mapState({
                inError: state => state.status.inError,
                msg: state => state.status.errorMessage
            }),
        },
        data() {
            return {
                username: '',
                password: '',
                disabled: false
            }
        },
        /* eslint-disable no-console */
        methods: {
            doAuth() {
                const {username, password} = this;
                this.$store.dispatch('profile/login', {username: username, password: password})
                this.disabled = true
            }
        }
    }
</script>

<style scoped lang="less">
    @import "~@leafygreen-ui/palette/dist/ui-colors";

    .v-btn {
        color: white;
        text-transform: none;
        font-size: 16px;
        height: 19px;
        width: 106px;
        font-weight: 600;
        line-height: 19px;
        text-align: center;
    }

    .hero {
        padding-top: 7em;
        background: white;
    }

    .hero-body {
        display: flex;
        flex-wrap: nowrap;
        justify-content: center;
        align-items: center;
    }

    .form-h1 {
        padding-top: 1em;
        text-align: center;
        font-size: 28px;
        font-weight: bold;
    }

    .form-p {
        text-align: center;
        color: @leafygreen__gray--dark-1;
        padding-bottom: 1em;
    }

    .form-input {
        width: 100%;
        margin-bottom: 1em;
        padding: 10px;
        border: 1px solid @leafygreen__gray--light-1;
        border-radius: 8px;
    }

</style>