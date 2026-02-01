<template>
  <div class="magic-form">
    <v-form ref="form" @submit.prevent="submitRegistration">
      <!-- Username -->
      <v-text-field
          id="username"
          v-model="username"
          label="Username"
          :error-messages="usernameErrors"
          :disabled="isLoading"
          required
          outlined
          dense
          class="magic-input"
          color="#B9FBC0"
          @blur="validateUsername"
      >
        <template v-slot:prepend-inner>
          <v-icon color="rgba(230, 225, 255, 0.5)">mdi-account-outline</v-icon>
        </template>
        <template v-slot:append v-if="username && !usernameErrors.length">
          <v-icon color="#B9FBC0">mdi-check-circle</v-icon>
        </template>
      </v-text-field>

      <!-- Email -->
      <v-text-field
          id="email"
          name="email"
          type="email"
          v-model="email"
          label="Email address"
          :error-messages="emailErrors"
          :disabled="isLoading"
          required
          outlined
          dense
          class="magic-input"
          color="#B9FBC0"
          @blur="validateEmail"
      >
        <template v-slot:prepend-inner>
          <v-icon color="rgba(230, 225, 255, 0.5)">mdi-email-outline</v-icon>
        </template>
        <template v-slot:append v-if="email && !emailErrors.length && isValidEmail">
          <v-icon color="#B9FBC0">mdi-check-circle</v-icon>
        </template>
      </v-text-field>

      <!-- Password with strength indicator -->
      <v-text-field
          id="password"
          name="password"
          :type="showPassword ? 'text' : 'password'"
          v-model="password"
          label="Password"
          :error-messages="passwordErrors"
          :disabled="isLoading"
          required
          outlined
          dense
          class="magic-input"
          color="#B9FBC0"
          @input="checkPasswordStrength"
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

      <!-- Password strength bar -->
      <div class="password-strength" v-if="password">
        <div class="strength-bar">
          <div
              class="strength-fill"
              :style="{ width: passwordStrength.percent + '%' }"
              :class="passwordStrength.class"
          ></div>
        </div>
        <span class="strength-label" :class="passwordStrength.class">
          {{ passwordStrength.label }}
        </span>
      </div>

      <!-- Confirm Password -->
      <v-text-field
          id="passwordConfirm"
          name="passwordConfirm"
          :type="showConfirmPassword ? 'text' : 'password'"
          v-model="registerPassConfirm"
          label="Confirm password"
          :error-messages="passwordConfirmErrors"
          :disabled="isLoading"
          required
          outlined
          dense
          class="magic-input"
          color="#B9FBC0"
          @blur="validatePasswordMatch"
      >
        <template v-slot:prepend-inner>
          <v-icon color="rgba(230, 225, 255, 0.5)">mdi-lock-check-outline</v-icon>
        </template>
        <template v-slot:append>
          <v-icon
              v-if="registerPassConfirm && password === registerPassConfirm"
              color="#B9FBC0"
          >
            mdi-check-circle
          </v-icon>
          <v-icon
              v-else
              color="rgba(230, 225, 255, 0.5)"
              @click="showConfirmPassword = !showConfirmPassword"
              style="cursor: pointer;"
          >
            {{ showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye' }}
          </v-icon>
        </template>
      </v-text-field>

      <!-- Optional: Name fields (collapsible) -->
      <v-expand-transition>
        <div v-if="showOptionalFields">
          <v-row dense>
            <v-col cols="6">
              <v-text-field
                  id="firstName"
                  name="firstName"
                  v-model="firstName"
                  label="First Name"
                  :disabled="isLoading"
                  outlined
                  dense
                  class="magic-input"
                  color="#B9FBC0"
              ></v-text-field>
            </v-col>
            <v-col cols="6">
              <v-text-field
                  id="lastName"
                  name="lastName"
                  v-model="lastName"
                  label="Last Name"
                  :disabled="isLoading"
                  outlined
                  dense
                  class="magic-input"
                  color="#B9FBC0"
              ></v-text-field>
            </v-col>
          </v-row>
        </div>
      </v-expand-transition>

      <button
          type="button"
          class="toggle-optional"
          @click="showOptionalFields = !showOptionalFields"
      >
        <v-icon small>{{ showOptionalFields ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
        {{ showOptionalFields ? 'Hide' : 'Add' }} optional details
      </button>

      <!-- Terms checkbox -->
      <v-checkbox
          v-model="acceptTerms"
          color="#B9FBC0"
          hide-details
          dense
          class="magic-checkbox mb-4"
      >
        <template v-slot:label>
          <span class="terms-label">
            I agree to the <a href="#" @click.prevent class="terms-link">Terms of Service</a>
            and <a href="#" @click.prevent class="terms-link">Privacy Policy</a>
          </span>
        </template>
      </v-checkbox>

      <v-btn
          class="magic-secondary"
          :loading="isLoading"
          :disabled="isLoading || !isFormValid"
          @click="submitRegistration()"
          block
          large
      >
        <v-icon left v-if="!isLoading">mdi-account-plus</v-icon>
        Create account
      </v-btn>
    </v-form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserRegistrationForm",
  props: ["setLoginResponseData"],
  data() {
    return {
      username: '',
      password: '',
      registerPassConfirm: '',
      firstName: '',
      lastName: '',
      email: '',
      usernameErrors: [],
      passwordErrors: [],
      passwordConfirmErrors: [],
      emailErrors: [],
      showPassword: false,
      showConfirmPassword: false,
      showOptionalFields: false,
      acceptTerms: false,
      isLoading: false,
      passwordStrength: {
        percent: 0,
        label: '',
        class: ''
      }
    }
  },
  computed: {
    isValidEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(this.email);
    },
    isFormValid() {
      return this.username.length >= 3 &&
          this.password.length >= 6 &&
          this.password === this.registerPassConfirm &&
          this.isValidEmail &&
          this.acceptTerms;
    }
  },
  methods: {
    validateUsername() {
      this.usernameErrors = [];
      if (this.username.length > 0 && this.username.length < 3) {
        this.usernameErrors.push('Username must be at least 3 characters');
      }
      if (this.username && !/^[a-zA-Z0-9_]+$/.test(this.username)) {
        this.usernameErrors.push('Only letters, numbers, and underscores');
      }
    },
    validateEmail() {
      this.emailErrors = [];
      if (this.email && !this.isValidEmail) {
        this.emailErrors.push('Please enter a valid email address');
      }
    },
    validatePasswordMatch() {
      this.passwordConfirmErrors = [];
      if (this.registerPassConfirm && this.password !== this.registerPassConfirm) {
        this.passwordConfirmErrors.push('Passwords do not match');
      }
    },
    checkPasswordStrength() {
      const pwd = this.password;
      let strength = 0;

      if (pwd.length >= 6) strength += 20;
      if (pwd.length >= 10) strength += 20;
      if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength += 20;
      if (/\d/.test(pwd)) strength += 20;
      if (/[^a-zA-Z0-9]/.test(pwd)) strength += 20;

      let label, className;
      if (strength <= 20) {
        label = 'Weak';
        className = 'weak';
      } else if (strength <= 40) {
        label = 'Fair';
        className = 'fair';
      } else if (strength <= 60) {
        label = 'Good';
        className = 'good';
      } else if (strength <= 80) {
        label = 'Strong';
        className = 'strong';
      } else {
        label = 'Excellent';
        className = 'excellent';
      }

      this.passwordStrength = { percent: strength, label, class: className };

      this.passwordErrors = [];
      if (pwd.length > 0 && pwd.length < 6) {
        this.passwordErrors.push('Password must be at least 6 characters');
      }
    },
    async submitRegistration() {
      if (!this.isFormValid) return;

      console.log('submitRegistration()');
      this.isLoading = true;

      const sendURL = '/api/user/auth/registration';

      let userCredits = {
        username: this.username,
        password: this.password,
        email: this.email,
        firstName: this.firstName,
        lastName: this.lastName
      };

      try {
        const response = await axios.post(sendURL, userCredits);
        console.log('response', response);

        if (this.setLoginResponseData) {
          this.setLoginResponseData('success', 'Account created! You can now sign in.', true);
        }

        // Clear form
        this.username = '';
        this.password = '';
        this.registerPassConfirm = '';
        this.email = '';
        this.firstName = '';
        this.lastName = '';
        this.acceptTerms = false;

      } catch (error) {
        console.log('Registration error:', error);
        let errorMessage = 'Registration failed. Please try again.';

        if (error.response) {
          if (error.response.status === 409) {
            errorMessage = 'Username or email already exists';
            this.usernameErrors = ['This username may already be taken'];
          } else if (error.response.status === 400) {
            errorMessage = 'Invalid registration data';
          }
        }

        if (this.setLoginResponseData) {
          this.setLoginResponseData('error', errorMessage, true);
        }
      } finally {
        this.isLoading = false;
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
  margin-bottom: 4px;
}

.magic-form ::v-deep .v-input__slot {
  background: rgba(255, 255, 255, 0.04) !important;
  border-radius: 14px !important;
  min-height: 48px !important;
}

.magic-form ::v-deep .v-text-field--outlined fieldset {
  border-color: rgba(255, 255, 255, 0.1);
  transition: border-color 0.25s ease;
}

.magic-form ::v-deep .v-text-field--outlined:hover fieldset {
  border-color: rgba(185, 251, 192, 0.3);
}

.magic-form ::v-deep .v-text-field--outlined.v-input--is-focused fieldset {
  border-color: #B9FBC0;
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

/* Password strength */
.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: -4px 0 12px 0;
  padding: 0 4px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-fill.weak { background: #ff6b6b; }
.strength-fill.fair { background: #ffa94d; }
.strength-fill.good { background: #ffd43b; }
.strength-fill.strong { background: #69db7c; }
.strength-fill.excellent { background: #B9FBC0; }

.strength-label {
  font-size: 0.75rem;
  min-width: 60px;
}

.strength-label.weak { color: #ff6b6b; }
.strength-label.fair { color: #ffa94d; }
.strength-label.good { color: #ffd43b; }
.strength-label.strong { color: #69db7c; }
.strength-label.excellent { color: #B9FBC0; }

/* Toggle optional fields */
.toggle-optional {
  background: none;
  border: none;
  color: rgba(230, 225, 255, 0.5);
  font-size: 0.85rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 8px 0 16px;
  padding: 0;
  transition: color 0.2s ease;
}

.toggle-optional:hover {
  color: rgba(230, 225, 255, 0.8);
}

/* Terms */
.magic-checkbox ::v-deep .v-label {
  color: rgba(230, 225, 255, 0.65) !important;
}

.terms-label {
  font-size: 0.85rem;
  color: rgba(230, 225, 255, 0.65);
}

.terms-link {
  color: #B9FBC0;
  text-decoration: none;
}

.terms-link:hover {
  text-decoration: underline;
}

/* Submit button */
.magic-secondary {
  background: linear-gradient(120deg, rgba(185, 251, 192, 0.95), rgba(165, 214, 167, 0.95)) !important;
  color: #0d1a14 !important;
  border-radius: 14px;
  font-weight: 600;
  font-size: 1rem;
  text-transform: none;
  letter-spacing: 0.02em;
  min-height: 52px;
  box-shadow: 0 4px 16px rgba(185, 251, 192, 0.2);
  transition: all 0.25s ease;
}

.magic-secondary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(185, 251, 192, 0.3);
}

.magic-secondary:disabled {
  opacity: 0.6;
}

.magic-secondary ::v-deep .v-btn__loader {
  color: #0d1a14;
}
</style>
