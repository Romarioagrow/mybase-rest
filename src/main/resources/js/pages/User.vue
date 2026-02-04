<template>
  <v-content class="profile-stage">
    <div class="profile-sky"></div>
    <v-container fluid class="profile-wrap">
      <!-- Header -->
      <v-row class="mb-6">
        <v-col cols="12">
          <div class="profile-kicker">MyBase / Profile</div>
          <h1 class="profile-title">
            Your <span class="profile-glow">Account</span>
          </h1>
        </v-col>
      </v-row>

      <v-row v-if="user">
        <!-- Left Column: Avatar & Basic Info -->
        <v-col cols="12" md="4">
          <v-card class="profile-card avatar-card" elevation="12">
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <v-avatar size="140" class="profile-avatar">
                  <img v-if="user.avatarUrl" :src="user.avatarUrl" alt="Avatar">
                  <v-icon v-else size="80" color="#f6d365">mdi-account-circle</v-icon>
                </v-avatar>
                <div class="avatar-badge" v-if="user.primaryProvider">
                  <v-icon small :color="getProviderColor(user.primaryProvider)">
                    {{ getProviderIcon(user.primaryProvider) }}
                  </v-icon>
                </div>
              </div>
              <h2 class="user-display-name">{{ user.name || user.username }}</h2>
              <p class="user-email">{{ user.email }}</p>
              <div class="user-role-badges">
                <span v-for="role in user.roles" :key="role" class="role-badge">
                  {{ formatRole(role) }}
                </span>
              </div>
            </div>

            <v-divider class="profile-divider"></v-divider>

            <div class="stats-section">
              <div class="stat-item">
                <v-icon small color="#f6d365">mdi-calendar-check</v-icon>
                <span class="stat-label">Member since</span>
                <span class="stat-value">{{ formatDate(user.createdAt) }}</span>
              </div>
              <div class="stat-item">
                <v-icon small color="#f6d365">mdi-shield-account</v-icon>
                <span class="stat-label">Auth provider</span>
                <span class="stat-value">{{ user.primaryProvider || 'Local' }}</span>
              </div>
            </div>

            <v-card-actions class="pa-4">
              <v-btn
                block
                class="logout-btn"
                @click="handleLogout"
                :loading="loggingOut"
              >
                <v-icon left>mdi-logout</v-icon>
                Sign Out
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>

        <!-- Right Column: Edit Profile & Settings -->
        <v-col cols="12" md="8">
          <!-- Edit Profile Card -->
          <v-card class="profile-card mb-4" elevation="12">
            <v-card-title class="card-title">
              <v-icon left color="#f6d365">mdi-account-edit</v-icon>
              Edit Profile
            </v-card-title>
            <v-card-text>
              <v-form ref="profileForm" v-model="formValid">
                <v-row>
                  <v-col cols="12" md="6">
                    <div class="input-label">Display Name</div>
                    <v-text-field
                      v-model="editForm.name"
                      placeholder="Your display name"
                      outlined
                      dense
                      dark
                      class="profile-input"
                      :rules="[rules.required]"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <div class="input-label">Username</div>
                    <v-text-field
                      v-model="editForm.username"
                      placeholder="@username"
                      outlined
                      dense
                      dark
                      class="profile-input"
                      :rules="[rules.required, rules.username]"
                      prefix="@"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <div class="input-label">Email</div>
                    <v-text-field
                      v-model="editForm.email"
                      placeholder="your@email.com"
                      outlined
                      dense
                      dark
                      class="profile-input"
                      :rules="[rules.required, rules.email]"
                      :disabled="isOAuthUser"
                    ></v-text-field>
                    <p v-if="isOAuthUser" class="input-hint">
                      Email is managed by your OAuth provider
                    </p>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
            <v-card-actions class="pa-4 pt-0">
              <v-spacer></v-spacer>
              <v-btn
                text
                class="cancel-btn"
                @click="resetForm"
                :disabled="!hasChanges"
              >
                Cancel
              </v-btn>
              <v-btn
                class="save-btn"
                @click="saveProfile"
                :loading="saving"
                :disabled="!hasChanges || !formValid"
              >
                <v-icon left small>mdi-content-save</v-icon>
                Save Changes
              </v-btn>
            </v-card-actions>
          </v-card>

          <!-- Connected Accounts Card -->
          <v-card class="profile-card mb-4" elevation="12">
            <v-card-title class="card-title">
              <v-icon left color="#f6d365">mdi-link-variant</v-icon>
              Connected Accounts
            </v-card-title>
            <v-card-text>
              <div class="connected-accounts">
                <!-- Google -->
                <div class="account-row" :class="{ connected: isProviderConnected('GOOGLE') }">
                  <div class="account-info">
                    <v-icon :color="isProviderConnected('GOOGLE') ? '#4285f4' : '#666'">
                      mdi-google
                    </v-icon>
                    <span class="account-name">Google</span>
                  </div>
                  <div class="account-status">
                    <span v-if="isProviderConnected('GOOGLE')" class="status-connected">
                      <v-icon small color="success">mdi-check-circle</v-icon>
                      Connected
                    </span>
                    <v-btn v-else small class="connect-btn" @click="connectGoogle">
                      Connect
                    </v-btn>
                  </div>
                </div>

                <!-- Facebook -->
                <div class="account-row" :class="{ connected: isProviderConnected('FACEBOOK') }">
                  <div class="account-info">
                    <v-icon :color="isProviderConnected('FACEBOOK') ? '#3b5998' : '#666'">
                      mdi-facebook
                    </v-icon>
                    <span class="account-name">Facebook</span>
                  </div>
                  <div class="account-status">
                    <span v-if="isProviderConnected('FACEBOOK')" class="status-connected">
                      <v-icon small color="success">mdi-check-circle</v-icon>
                      Connected
                    </span>
                    <v-btn v-else small class="connect-btn" @click="connectFacebook" disabled>
                      Coming Soon
                    </v-btn>
                  </div>
                </div>

                <!-- Instagram -->
                <div class="account-row" :class="{ connected: isProviderConnected('INSTAGRAM') }">
                  <div class="account-info">
                    <v-icon :color="isProviderConnected('INSTAGRAM') ? '#d6249f' : '#666'">
                      mdi-instagram
                    </v-icon>
                    <span class="account-name">Instagram</span>
                  </div>
                  <div class="account-status">
                    <span v-if="isProviderConnected('INSTAGRAM')" class="status-connected">
                      <v-icon small color="success">mdi-check-circle</v-icon>
                      Connected
                    </span>
                    <v-btn v-else small class="connect-btn" @click="connectInstagram" disabled>
                      Coming Soon
                    </v-btn>
                  </div>
                </div>
              </div>
            </v-card-text>
          </v-card>

          <!-- Security Card (only for local accounts) -->
          <v-card v-if="!isOAuthUser" class="profile-card" elevation="12">
            <v-card-title class="card-title">
              <v-icon left color="#f6d365">mdi-shield-lock</v-icon>
              Security
            </v-card-title>
            <v-card-text>
              <v-form ref="passwordForm" v-model="passwordFormValid">
                <v-row>
                  <v-col cols="12" md="6">
                    <div class="input-label">Current Password</div>
                    <v-text-field
                      v-model="passwordForm.currentPassword"
                      :type="showCurrentPassword ? 'text' : 'password'"
                      placeholder="Enter current password"
                      outlined
                      dense
                      dark
                      class="profile-input"
                      :append-icon="showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'"
                      @click:append="showCurrentPassword = !showCurrentPassword"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6"></v-col>
                  <v-col cols="12" md="6">
                    <div class="input-label">New Password</div>
                    <v-text-field
                      v-model="passwordForm.newPassword"
                      :type="showNewPassword ? 'text' : 'password'"
                      placeholder="Enter new password"
                      outlined
                      dense
                      dark
                      class="profile-input"
                      :rules="[rules.minLength]"
                      :append-icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                      @click:append="showNewPassword = !showNewPassword"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" md="6">
                    <div class="input-label">Confirm New Password</div>
                    <v-text-field
                      v-model="passwordForm.confirmPassword"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      placeholder="Confirm new password"
                      outlined
                      dense
                      dark
                      class="profile-input"
                      :rules="[rules.passwordMatch]"
                      :append-icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
                      @click:append="showConfirmPassword = !showConfirmPassword"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
            <v-card-actions class="pa-4 pt-0">
              <v-spacer></v-spacer>
              <v-btn
                class="save-btn"
                @click="changePassword"
                :loading="changingPassword"
                :disabled="!canChangePassword"
              >
                <v-icon left small>mdi-lock-reset</v-icon>
                Update Password
              </v-btn>
            </v-card-actions>
          </v-card>

          <!-- Danger Zone -->
          <v-card class="profile-card danger-card mt-4" elevation="12">
            <v-card-title class="card-title danger">
              <v-icon left color="#ff5252">mdi-alert-circle</v-icon>
              Danger Zone
            </v-card-title>
            <v-card-text>
              <div class="danger-row">
                <div class="danger-info">
                  <h4>Delete Account</h4>
                  <p>Permanently delete your account and all associated data. This action cannot be undone.</p>
                </div>
                <v-btn class="delete-btn" @click="confirmDeleteAccount">
                  Delete Account
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Not logged in state -->
      <v-row v-else>
        <v-col cols="12" class="text-center">
          <v-card class="profile-card pa-8" elevation="12">
            <v-icon size="80" color="#f6d365">mdi-account-lock</v-icon>
            <h2 class="mt-4 mb-2" style="color: #f7f4ff;">Not Signed In</h2>
            <p style="color: rgba(230, 225, 255, 0.7);">Please sign in to view your profile</p>
            <v-btn class="save-btn mt-4" @click="goToAuth">
              <v-icon left>mdi-login</v-icon>
              Sign In
            </v-btn>
          </v-card>
        </v-col>
      </v-row>

      <!-- Snackbar for notifications -->
      <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" top>
        {{ snackbar.message }}
        <template v-slot:action="{ attrs }">
          <v-btn text v-bind="attrs" @click="snackbar.show = false">Close</v-btn>
        </template>
      </v-snackbar>

      <!-- Delete confirmation dialog -->
      <v-dialog v-model="deleteDialog" max-width="450">
        <v-card class="profile-card">
          <v-card-title class="card-title danger">
            <v-icon left color="#ff5252">mdi-alert</v-icon>
            Confirm Account Deletion
          </v-card-title>
          <v-card-text class="pt-4">
            <p style="color: rgba(230, 225, 255, 0.85);">
              Are you sure you want to delete your account? This action is permanent and cannot be undone.
            </p>
            <v-text-field
              v-model="deleteConfirmText"
              placeholder="Type DELETE to confirm"
              outlined
              dense
              dark
              class="profile-input mt-4"
            ></v-text-field>
          </v-card-text>
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn text class="cancel-btn" @click="deleteDialog = false">Cancel</v-btn>
            <v-btn
              class="delete-btn"
              @click="deleteAccount"
              :disabled="deleteConfirmText !== 'DELETE'"
              :loading="deleting"
            >
              Delete Forever
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </v-content>
</template>

<script>
import axios from 'axios'

export default {
  name: "UserProfile",
  data() {
    return {
      formValid: false,
      passwordFormValid: false,
      saving: false,
      loggingOut: false,
      changingPassword: false,
      deleting: false,
      deleteDialog: false,
      deleteConfirmText: '',
      showCurrentPassword: false,
      showNewPassword: false,
      showConfirmPassword: false,
      editForm: {
        name: '',
        username: '',
        email: ''
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      snackbar: {
        show: false,
        message: '',
        color: 'success'
      },
      rules: {
        required: v => !!v || 'This field is required',
        email: v => /.+@.+\..+/.test(v) || 'Invalid email address',
        username: v => /^[a-zA-Z0-9_]+$/.test(v) || 'Only letters, numbers, and underscores',
        minLength: v => !v || v.length >= 6 || 'Minimum 6 characters',
        passwordMatch: v => v === this.passwordForm.newPassword || 'Passwords do not match'
      }
    }
  },
  computed: {
    user() {
      return this.$store.state.auth.user
    },
    isOAuthUser() {
      return this.user && this.user.primaryProvider && this.user.primaryProvider !== 'LOCAL'
    },
    hasChanges() {
      if (!this.user) return false
      return this.editForm.name !== this.user.name ||
             this.editForm.username !== this.user.username ||
             this.editForm.email !== this.user.email
    },
    canChangePassword() {
      return this.passwordForm.currentPassword &&
             this.passwordForm.newPassword &&
             this.passwordForm.newPassword.length >= 6 &&
             this.passwordForm.newPassword === this.passwordForm.confirmPassword
    }
  },
  watch: {
    user: {
      immediate: true,
      handler(newUser) {
        if (newUser) {
          this.editForm.name = newUser.name || ''
          this.editForm.username = newUser.username || ''
          this.editForm.email = newUser.email || ''
        }
      }
    }
  },
  created() {
    // Check if user is authenticated
    if (!this.$store.state.auth.isAuthenticated) {
      this.$store.dispatch('checkAuthStatus')
    }
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return 'N/A'
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    formatRole(role) {
      if (!role) return ''
      return role.replace('ROLE_', '').charAt(0) + role.replace('ROLE_', '').slice(1).toLowerCase()
    },
    getProviderIcon(provider) {
      const icons = {
        GOOGLE: 'mdi-google',
        FACEBOOK: 'mdi-facebook',
        INSTAGRAM: 'mdi-instagram',
        LOCAL: 'mdi-email'
      }
      return icons[provider] || 'mdi-account'
    },
    getProviderColor(provider) {
      const colors = {
        GOOGLE: '#4285f4',
        FACEBOOK: '#3b5998',
        INSTAGRAM: '#d6249f',
        LOCAL: '#f6d365'
      }
      return colors[provider] || '#f6d365'
    },
    isProviderConnected(provider) {
      return this.user && this.user.primaryProvider === provider
    },
    resetForm() {
      if (this.user) {
        this.editForm.name = this.user.name || ''
        this.editForm.username = this.user.username || ''
        this.editForm.email = this.user.email || ''
      }
    },
    async saveProfile() {
      if (!this.$refs.profileForm.validate()) return

      this.saving = true
      try {
        const token = localStorage.getItem('accessToken')
        const response = await axios.put('/api/user/profile', this.editForm, {
          headers: { Authorization: `Bearer ${token}` }
        })

        // Update store with new user data
        await this.$store.dispatch('fetchCurrentUser')

        this.showSnackbar('Profile updated successfully', 'success')
      } catch (error) {
        const message = error.response?.data?.error || 'Failed to update profile'
        this.showSnackbar(message, 'error')
      } finally {
        this.saving = false
      }
    },
    async changePassword() {
      if (!this.canChangePassword) return

      this.changingPassword = true
      try {
        const token = localStorage.getItem('accessToken')
        await axios.put('/api/user/password', {
          currentPassword: this.passwordForm.currentPassword,
          newPassword: this.passwordForm.newPassword
        }, {
          headers: { Authorization: `Bearer ${token}` }
        })

        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' }
        this.showSnackbar('Password changed successfully', 'success')
      } catch (error) {
        const message = error.response?.data?.error || 'Failed to change password'
        this.showSnackbar(message, 'error')
      } finally {
        this.changingPassword = false
      }
    },
    async handleLogout() {
      this.loggingOut = true
      try {
        await this.$store.dispatch('doLogout')
        this.$router.push('/auth')
      } finally {
        this.loggingOut = false
      }
    },
    confirmDeleteAccount() {
      this.deleteConfirmText = ''
      this.deleteDialog = true
    },
    async deleteAccount() {
      if (this.deleteConfirmText !== 'DELETE') return

      this.deleting = true
      try {
        const token = localStorage.getItem('accessToken')
        await axios.delete('/api/user/account', {
          headers: { Authorization: `Bearer ${token}` }
        })

        await this.$store.dispatch('doLogout')
        this.$router.push('/auth')
        this.showSnackbar('Account deleted successfully', 'success')
      } catch (error) {
        const message = error.response?.data?.error || 'Failed to delete account'
        this.showSnackbar(message, 'error')
      } finally {
        this.deleting = false
        this.deleteDialog = false
      }
    },
    connectGoogle() {
      this.$store.dispatch('doGoogleAuth')
    },
    connectFacebook() {
      this.$store.dispatch('doFacebookAuth')
    },
    connectInstagram() {
      this.$store.dispatch('doInstagramAuth')
    },
    goToAuth() {
      this.$router.push('/auth')
    },
    showSnackbar(message, color) {
      this.snackbar.message = message
      this.snackbar.color = color
      this.snackbar.show = true
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Space+Grotesk:wght@300;400;600&display=swap');

.profile-stage {
  min-height: calc(100vh - 64px);
  position: relative;
  overflow: hidden;
  background: radial-gradient(1200px 800px at 15% 10%, #2a1a47 0%, #0f1027 45%, #090b15 100%);
}

.profile-sky {
  position: absolute;
  inset: -20% -10%;
  background:
    radial-gradient(2px 2px at 12% 20%, rgba(255,255,255,0.45) 40%, transparent 60%),
    radial-gradient(2px 2px at 78% 30%, rgba(255,255,255,0.35) 40%, transparent 60%),
    radial-gradient(1px 1px at 55% 80%, rgba(255,255,255,0.5) 40%, transparent 60%);
  opacity: 0.5;
  animation: drift 34s linear infinite;
}

.profile-wrap {
  position: relative;
  z-index: 2;
  padding-top: 32px;
  padding-bottom: 64px;
}

.profile-kicker {
  font-family: "Space Grotesk", sans-serif;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  font-size: 0.75rem;
  color: #b9b5ff;
  margin-bottom: 8px;
}

.profile-title {
  font-family: "Cinzel", serif;
  font-size: clamp(2rem, 4vw, 3rem);
  line-height: 1.1;
  color: #f7f4ff;
  margin: 0;
}

.profile-glow {
  color: #f6d365;
  text-shadow: 0 0 18px rgba(246, 211, 101, 0.45);
}

.profile-card {
  background: rgba(12, 14, 30, 0.92) !important;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 20px;
  overflow: hidden;
}

.avatar-card {
  text-align: center;
}

.avatar-section {
  padding: 32px 24px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.profile-avatar {
  border: 3px solid rgba(246, 211, 101, 0.5);
  box-shadow: 0 0 30px rgba(246, 211, 101, 0.2);
}

.avatar-badge {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 32px;
  height: 32px;
  background: rgba(12, 14, 30, 0.95);
  border: 2px solid rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-display-name {
  font-family: "Cinzel", serif;
  font-size: 1.5rem;
  color: #f7f4ff;
  margin: 0 0 4px 0;
}

.user-email {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.6);
  font-size: 0.9rem;
  margin: 0;
}

.user-role-badges {
  margin-top: 12px;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(120deg, rgba(246, 211, 101, 0.2), rgba(253, 160, 133, 0.2));
  border: 1px solid rgba(246, 211, 101, 0.3);
  border-radius: 20px;
  font-family: "Space Grotesk", sans-serif;
  font-size: 0.75rem;
  color: #f6d365;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.profile-divider {
  border-color: rgba(255,255,255,0.08) !important;
  margin: 0 24px;
}

.stats-section {
  padding: 20px 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  color: rgba(230, 225, 255, 0.7);
  font-family: "Space Grotesk", sans-serif;
  font-size: 0.85rem;
}

.stat-label {
  flex: 1;
}

.stat-value {
  color: #f7f4ff;
  font-weight: 500;
}

.card-title {
  font-family: "Cinzel", serif;
  color: #f7f4ff;
  font-size: 1.2rem;
  padding: 20px 24px 12px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.card-title.danger {
  color: #ff5252;
}

.input-label {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.7);
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 8px;
}

.input-hint {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.5);
  font-size: 0.75rem;
  margin-top: -12px;
  font-style: italic;
}

.profile-input >>> .v-input__slot {
  background: rgba(255, 255, 255, 0.04) !important;
  border-radius: 12px !important;
}

.profile-input >>> .v-text-field__slot input {
  color: #f7f4ff !important;
}

.profile-input >>> .v-text-field__slot input::placeholder {
  color: rgba(230, 225, 255, 0.4) !important;
}

.save-btn {
  background: linear-gradient(120deg, #f6d365, #fda085) !important;
  color: #1b1833 !important;
  font-family: "Space Grotesk", sans-serif;
  font-weight: 600;
  text-transform: none;
  border-radius: 12px;
  padding: 0 24px;
}

.cancel-btn {
  color: rgba(230, 225, 255, 0.7) !important;
  font-family: "Space Grotesk", sans-serif;
  text-transform: none;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.06) !important;
  color: rgba(230, 225, 255, 0.8) !important;
  font-family: "Space Grotesk", sans-serif;
  text-transform: none;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.1);
}

.logout-btn:hover {
  background: rgba(255, 82, 82, 0.15) !important;
  border-color: rgba(255, 82, 82, 0.3);
  color: #ff5252 !important;
}

/* Connected Accounts */
.connected-accounts {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.account-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.account-row.connected {
  border-color: rgba(76, 175, 80, 0.3);
  background: rgba(76, 175, 80, 0.05);
}

.account-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.account-name {
  font-family: "Space Grotesk", sans-serif;
  color: #f7f4ff;
  font-weight: 500;
}

.status-connected {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: "Space Grotesk", sans-serif;
  color: #4caf50;
  font-size: 0.85rem;
}

.connect-btn {
  background: rgba(255, 255, 255, 0.08) !important;
  color: #f7f4ff !important;
  font-family: "Space Grotesk", sans-serif;
  text-transform: none;
  border-radius: 8px;
}

/* Danger Zone */
.danger-card {
  border-color: rgba(255, 82, 82, 0.2) !important;
}

.danger-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.danger-info h4 {
  font-family: "Space Grotesk", sans-serif;
  color: #f7f4ff;
  margin: 0 0 4px 0;
}

.danger-info p {
  font-family: "Space Grotesk", sans-serif;
  color: rgba(230, 225, 255, 0.6);
  font-size: 0.85rem;
  margin: 0;
}

.delete-btn {
  background: rgba(255, 82, 82, 0.15) !important;
  color: #ff5252 !important;
  font-family: "Space Grotesk", sans-serif;
  text-transform: none;
  border-radius: 12px;
  border: 1px solid rgba(255, 82, 82, 0.3) !important;
  flex-shrink: 0;
}

.delete-btn:hover {
  background: rgba(255, 82, 82, 0.25) !important;
}

@keyframes drift {
  0% { transform: translate3d(0, 0, 0); }
  50% { transform: translate3d(4%, -2%, 0); }
  100% { transform: translate3d(0, 0, 0); }
}

@media (max-width: 960px) {
  .danger-row {
    flex-direction: column;
    align-items: flex-start;
  }
  .delete-btn {
    width: 100%;
    margin-top: 12px;
  }
}
</style>
