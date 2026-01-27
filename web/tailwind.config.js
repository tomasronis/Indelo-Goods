/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        mustard: '#F4C430',
        ketchup: '#E63946',
        bun: '#FFF8DC',
        charcoal: '#2B2D42',
      },
      fontFamily: {
        pixel: ['"Press Start 2P"', 'cursive'],
      },
    },
  },
  plugins: [],
}
