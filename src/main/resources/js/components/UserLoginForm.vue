<template>
  <div class="magic-form">
    <v-form @submit.prevent="loginUser">
      <v-text-field
          id="username"
          name="username"
          color="#F6D365"
          v-model="userLogin"
          label="Username or Email"
          :error-messages="usernameErrors"
          :disabled="isLoading"
          required
          outlined
          dense
          class="magic-input"
      >
        <template v-slot:prepend-inner>
          <v-icon color="rgba(230, 225, 255, 0.5)">mdi-account-outline</v-icon>
        </template>
      </v-text-field>

      <v-text-field
          id="password"
          name="password"
          color="#F6D365"
          :type="showPassword ? 'text' : 'password'"
          v-model="userPassword"
          label="Password"
          :error-messages="passwordErrors"
          :disabled="isLoading"
          required
          outlined
          dense
          class="magic-input"
      >
        <template v-slot:prepend-inner>
          <v-icon color="rgba(230, 225, 255, 0.5)">mdi-lock-outline</v-icon>
        </template>
        <template v-slot:append>
          <v-icon
              color="rgba(230, 225, 255, 0.5)"
              @click="showPassword = !showPassword"
              style="cursor: pointer;"
          >
            {{ showPassword ? 'mdi-eye-off' : 'mdi-eye' }}
          </v-icon>
        </template>
      </v-text-field>

      <div class="magic-options">
        <v-checkbox
            v-model="rememberMe"
            label="Remember me"
            color="#F6D365"
            hide-details
            dense
            class="magic-checkbox"
        ></v-checkbox>
        <a href="#" class="magic-forgot" @click.prevent="forgotPassword">
          Forgot password?
        </a>
      </div>

      <v-btn
          class="magic-primary"
          :loading="isLoading"
          :disabled="isLoading || !isFormValid"
          @click="loginUser()"
          block
          large
      >
        <v-icon left v-if="!isLoading">mdi-login</v-icon>
        Sign in
      </v-btn>
    </v-form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserLoginForm",
  props: ["clearLoginResponse", "setLoginResponseData"],
  data() {
    return {
      userLogin: '',
      userPassword: '',
      passwordErrors: [],
      usernameErrors: [],
      showPassword: false,
      rememberMe: false,
      isLoading: false,
    }
  },
  computed: {
    isFormValid() {
      return this.userLogin.length > 0 && this.userPassword.length > 0;
    }
  },
  methods: {
    forgotPassword() {
      this.setLoginResponseData('info', 'Password reset feature coming soon', true);
    },
    async loginUser() {
      if (!this.isFormValid) return;

      console.log('loginUser()');
      this.clearLoginResponse();
      this.isLoading = true;
      this.usernameErrors = [];
      this.passwordErrors = [];

      let auth = new FormData();
      auth.set('username', this.userLogin);
      auth.set('password', this.userPassword);

      const config = {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      };

      const loginURL = '/user/login';

      try {
        const response = await axios.post(loginURL, auth, config);
        console.log('authResponse', response);
        this.$store.dispatch('authUser', response.data);
        this.handleSuccessfulLoginResponse(response);
      } catch (error) {
        console.log('catch login error', error);
        this.handleErrorLoginResponse(error);
      } finally {
        this.isLoading = false;
      }
    },
    handleSuccessfulLoginResponse(response) {
      console.log('handleSuccessfulLoginResponse', response);
      if (response) {
        this.setLoginResponseData('success', 'Login successful! Redirecting...', true);
        setTimeout(() => {
          this.$router.push('/');
        }, 1000);
      } else {
        this.setLoginResponseData('error', 'Login error: no response', true);
      }
    },
    handleErrorLoginResponse(error) {
      console.log('handleErrorLoginResponse');

      if (error.response) {
        const errorStatus = error.response.status;
        let errorMessage;

        switch (errorStatus) {
          case 401:
            errorMessage = 'Invalid username or password';
            this.passwordErrors = ['Please check your credentials'];
            break;
          case 403:
            errorMessage = 'Account is disabled';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later';
            break;
          default:
            errorMessage = 'An error occurred. Please try again';
        }
        this.setLoginResponseData('error', errorMessage, true);
      } else {
        this.setLoginResponseData('error', 'Network error. Check your connection', true);
      }
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

.magic-input {
  margin-bottom: 8px;
}

.magic-form ::v-deep .v-input__slot {
  background: rgba(255, 255, 255, 0.04) !important;
  border-radius: 14px !important;
  min-height: 52px !important;
}

.magic-form ::v-deep .v-text-field--outlined fieldset {
  border-color: rgba(255, 255, 255, 0.1);
  transition: border-color 0.25s ease;
}

.magic-form ::v-deep .v-text-field--outlined:hover fieldset {
  border-color: rgba(246, 211, 101, 0.3);
}

.magic-form ::v-deep .v-text-field--outlined.v-input--is-focused fieldset {
  border-color: #f6d365;
  border-width: 2px;
}

.magic-form ::v-deep .v-input input {
  color: #f7f4ff !important;
  padding: 8px 4px !important;
}

.magic-form ::v-deep .v-label {
  color: rgba(230, 225, 255, 0.6) !important;
}

.magic-form ::v-deep .v-messages__message {
  color: #ff6b6b !important;
}

.magic-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 8px;
}

.magic-checkbox ::v-deep .v-label {
  color: rgba(230, 225, 255, 0.65) !important;
  font-size: 0.9rem;
}

.magic-checkbox ::v-deep .v-input--selection-controls__input {
  margin-right: 8px;
}

.magic-forgot {
  color: #f6d365;
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.magic-forgot:hover {
  color: #fda085;
  text-decoration: underline;
}

.magic-primary {
  background: linear-gradient(120deg, #f6d365, #fda085) !important;
  color: #1b1833 !important;
  border-radius: 14px;
  font-weight: 600;
  font-size: 1rem;
  text-transform: none;
  letter-spacing: 0.02em;
  min-height: 52px;
  box-shadow: 0 4px 16px rgba(246, 211, 101, 0.25);
  transition: all 0.25s ease;
}

.magic-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(246, 211, 101, 0.35);
}

.magic-primary:disabled {
  opacity: 0.6;
}

.magic-primary ::v-deep .v-btn__loader {
  color: #1b1833;
}
</style>
