/**
  * Tailwind.config.js is read by the tailwindcss cli when Gradle invokes it as part of the build.
  *
  * See "tailwind" task from build.gradle.kts
**/

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: {
      files: [
          './src/main/kotlin/**/*.kt',
      ]
  },
  theme: {
    extend: {},
  },
  plugins: [],
}