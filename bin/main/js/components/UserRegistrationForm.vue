<template>
  <div class="magic-form">
    <v-form>
      <v-text-field id="username"
                    color="#B9FBC0"
                    label="Username"
                    required
                    prepend-icon="mdi-account"
                    v-model="username"
                    :error-messages="usernameErrors"
      ></v-text-field>
      <v-row>
        <v-col>
          <v-text-field id="password"
                        name="password"
                        prepend-icon="mdi-key"
                        type="password"
                        label="Password"
                        required
                        color="#B9FBC0"
                        v-model="password"
                        :error-messages="passwordErrors"
          ></v-text-field>
        </v-col>
        <v-col>
          <v-text-field id="passwordConfirm"
                        name="passwordConfirm"
                        type="password"
                        label="Confirm password"
                        color="#B9FBC0"
                        v-model="registerPassConfirm"
                        :error-messages="passwordConfirmErrors"
          ></v-text-field>
        </v-col>
      </v-row>

      <v-row>
        <v-col>
          <v-text-field id="lastName"
                        name="lastName"
                        label="Last Name"
                        color="#B9FBC0"
                        prepend-icon="mdi-account-tie"
                        v-model="lastName"
                        :error-messages="lastNameErrors"
          ></v-text-field>
        </v-col>

        <v-col>
          <v-text-field id="firstName"
                        name="firstName"
                        type="text"
                        v-model="firstName"
                        label="First Name"
                        color="#B9FBC0"
                        :error-messages="firstNameErrors"
          ></v-text-field>
        </v-col>
      </v-row>

      <v-text-field id="email"
                    name="email"
                    prepend-icon="mdi-email"
                    type="email"
                    v-model="email"
                    color="#B9FBC0"
                    label="E-mail"
      ></v-text-field>
    </v-form>
    <v-btn class="magic-secondary" @click="submitRegistration()" block>
      Create account
    </v-btn>
    <div class="magic-form-hint">Your profile becomes a living signal map.</div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserRegistrationForm",
  data() {
    return {
      username: '',
      password: '',
      patronymic: '',
      firstName: '',
      lastName: '',
      email: '',
      usernameErrors: [],
      passwordErrors: [],
      registerPassConfirm: [],
      passwordConfirmErrors: [],
      patronymicErrors: [],
      firstNameErrors: [],
      lastNameErrors: [],
    }
  },
  methods: {
    submitRegistration() {
      console.log('submitRegistration()')
      const sendURL = '/api/user/auth/registration'

      let userCredits = {
        username: this.username,
        password: this.password,
        email: this.email
      }

      axios.post(sendURL, userCredits).then(response => {
        console.log('response', response);
        this.user = response.data
      })
    },
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;600&display=swap');

.magic-form {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.9);
}

.magic-form ::v-deep .v-input input {
  color: #f7f4ff !important;
}

.magic-form ::v-deep .v-label,
.magic-form ::v-deep .v-icon,
.magic-form ::v-deep .v-messages__message {
  color: rgba(230, 225, 255, 0.75) !important;
}

.magic-form ::v-deep .v-input__slot {
  background: rgba(255, 255, 255, 0.04);
  border-radius: 12px;
  padding: 4px 10px;
}

.magic-form ::v-deep .v-input input {
  padding: 8px 4px !important;
}

.magic-secondary {
  background: linear-gradient(120deg, rgba(185, 251, 192, 0.9), rgba(165, 214, 167, 0.9)) !important;
  color: #0d1a14 !important;
  border-radius: 14px;
  font-weight: 600;
  text-transform: none;
}

.magic-form-hint {
  margin-top: 10px;
  font-size: 0.8rem;
  color: rgba(230, 225, 255, 0.65);
}
</style>
