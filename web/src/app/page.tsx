import Link from 'next/link'

export default function Home() {
  return (
    <main className="min-h-screen bg-bun">
      {/* Hero Section */}
      <div className="flex flex-col items-center justify-center p-8 pt-20">
        <div className="max-w-2xl text-center mb-12">
          <h1 className="text-6xl font-bold mb-4 text-ketchup">🌭</h1>
          <h1 className="text-4xl font-bold mb-4 text-charcoal">Indelo Goods</h1>
          <p className="text-xl mb-8 text-charcoal/70">
            Discover quality canned goods and beverages by scanning QR codes in your favorite shops.
          </p>

          <div className="pixel-border bg-white p-8 mb-8">
            <h2 className="text-2xl font-bold mb-4 text-charcoal">How It Works</h2>
            <ol className="text-left space-y-4">
              <li className="flex gap-4">
                <span className="text-mustard font-bold">1.</span>
                <span className="text-charcoal">Scan a product QR code at a participating shop</span>
              </li>
              <li className="flex gap-4">
                <span className="text-mustard font-bold">2.</span>
                <span className="text-charcoal">View product details, ingredients, and certifications</span>
              </li>
              <li className="flex gap-4">
                <span className="text-mustard font-bold">3.</span>
                <span className="text-charcoal">Purchase directly through the app or online</span>
              </li>
            </ol>
          </div>

          <div className="space-y-4 mb-12">
            <button className="pixel-button w-full sm:w-auto">
              Download the App
            </button>
            <p className="text-sm text-charcoal/60">
              Available on Android • iOS Coming Soon
            </p>
          </div>
        </div>

        {/* Quick Links Section */}
        <div className="max-w-4xl w-full grid grid-cols-1 md:grid-cols-2 gap-6 px-8">
          <Link
            href="/products"
            className="pixel-border bg-white p-8 hover:bg-mustard/10 transition-colors text-center group"
          >
            <div className="text-4xl mb-4">🥫</div>
            <h3 className="text-2xl font-bold mb-2 text-charcoal">Browse Products</h3>
            <p className="text-charcoal/70">
              Explore our selection of quality canned goods and beverages
            </p>
          </Link>

          <Link
            href="/shops"
            className="pixel-border bg-white p-8 hover:bg-ketchup/10 transition-colors text-center group"
          >
            <div className="text-4xl mb-4">🏪</div>
            <h3 className="text-2xl font-bold mb-2 text-charcoal">Find Shops</h3>
            <p className="text-charcoal/70">
              Discover local shops carrying Indelo Goods products
            </p>
          </Link>
        </div>

        {/* Footer */}
        <div className="mt-16 text-center text-charcoal/60">
          <p className="text-sm">
            Supporting local producers and connecting them with shoppers
          </p>
        </div>
      </div>
    </main>
  )
}
