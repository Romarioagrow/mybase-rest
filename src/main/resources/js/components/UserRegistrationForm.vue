<template>
  <v-container>
    <v-card-text>
      <v-row justify="center">
        <span>First time here?</span>
      </v-row>
    </v-card-text>
    <!--                  -->
    <v-card-actions>
      <v-dialog v-model="registrationDialog" persistent max-width="500">
        <template v-slot:activator="{ on }">
          <v-btn color="purple" outlined block dark v-on="on" @click="registrationError = false">
            <span>Sign up</span>
          </v-btn>
        </template>
        <v-card>
          <v-card-title class="headline">
            <v-icon>
              mdi-account-plus
            </v-icon>
            <span class="ml-3">Registration</span>
          </v-card-title>

          <v-card-text>Sign up for your account</v-card-text>

          <v-card-actions>


            <v-form>
              <v-text-field id="username"
                            color="purple"
                            label="Username"
                            required
                            prepend-icon="mdi-account"
                            v-model="username"
                            :error-messages="usernameErrors"
              ></v-text-field>
              <!--@input="$v.username.$touch()"
                                                      @blur="$v.username.$touch()"                          -->
              <v-row>
                <v-col>
                  <v-text-field id="password"
                                name="password"
                                prepend-icon="mdi-key"
                                type="password"
                                label="Password"
                                required
                                color="purple"
                                v-model="password"
                                :error-messages="passwordErrors"
                  ></v-text-field>
                  <!--                                                                          @input="$v.password.$touch()"
                                                              @blur="$v.password.$touch()"-->
                </v-col>
                <v-col>
                  <v-text-field id="passwordConfirm"
                                name="passwordConfirm"
                                type="password"
                                label="Confirm password"
                                color="purple"
                                v-model="registerPassConfirm"
                                :error-messages="passwordConfirmErrors"
                  ></v-text-field>
                </v-col>
                <!--       @input="$v.registerPassConfirm.$touch()"
                                                            @blur="$v.registerPassConfirm.$touch()"                            -->
              </v-row>

              <v-row>
                <v-col>
                  <v-text-field id="lastName"
                                name="lastName"
                                label="Last Name"
                                color="purple"
                                prepend-icon="mdi-account-tie"
                                v-model="lastName"
                                :error-messages="lastNameErrors"
                  ></v-text-field>
<!--                  prepend-inner-icon="mdi-account"-->
                  <!--        @input="$v.lastName.$touch()"
                                                              @blur="$v.lastName.$touch()"                              -->
                </v-col>

                <v-col>
                  <v-text-field id="firstName"
                                name="firstName"
                                type="text"
                                v-model="firstName"
                                label="First Name"
                                color="purple"
                                :error-messages="firstNameErrors"
                  ></v-text-field>
                  <!--                         @input="$v.firstName.$touch()"
                                                              @blur="$v.firstName.$touch()"                          -->
                </v-col>

<!--                <v-col>
                  <v-text-field id="patronymic"
                                name="patronymic"
                                type="text"
                                v-model="patronymic"
                                label="Middle Name"
                                :error-messages="patronymicErrors"
                                color="purple"
                  ></v-text-field>
                  &lt;!&ndash;                                          @input="$v.patronymic.$touch()"
                                                              @blur="$v.patronymic.$touch()"                              &ndash;&gt;
                </v-col>-->
              </v-row>

              <v-text-field id="email"
                            name="email"
                            prepend-icon="mdi-email"
                            type="email"
                            v-model="email"
                            color="purple"
                            label="E-mail"
              ></v-text-field>
            </v-form>
          </v-card-actions>

          <v-card-actions>
            <div class="flex-grow-1"></div>
            <v-btn color="green darken-1" text @click="submitRegistration()">Sign up</v-btn>
            <v-btn color="red darken-1" text @click="registrationDialog = false">Cancel</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card-actions>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "UserRegistrationForm",
  data() {
    return {
      registrationDialog: false,
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

</style>