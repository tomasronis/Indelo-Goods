import Link from 'next/link'

export default function Home() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center p-8">
      <div className="max-w-2xl text-center">
        <h1 className="text-6xl font-bold mb-4 text-ketchup">🌭</h1>
        <h1 className="text-4xl font-bold mb-4">Indelo Goods</h1>
        <p className="text-xl mb-8 text-charcoal/70">
          Discover quality canned goods and beverages by scanning QR codes in your favorite shops.
        </p>

        <div className="pixel-border bg-white p-8 mb-8">
          <h2 className="text-2xl font-bold mb-4">How It Works</h2>
          <ol className="text-left space-y-4">
            <li className="flex gap-4">
              <span className="text-mustard font-bold">1.</span>
              <span>Scan a product QR code at a participating shop</span>
            </li>
            <li className="flex gap-4">
              <span className="text-mustard font-bold">2.</span>
              <span>View product details, ingredients, and certifications</span>
            </li>
            <li className="flex gap-4">
              <span className="text-mustard font-bold">3.</span>
              <span>Purchase directly through the app</span>
            </li>
          </ol>
        </div>

        <div className="space-y-4">
          <button className="pixel-button w-full sm:w-auto">
            Download the App
          </button>
          <p className="text-sm text-charcoal/60">
            Available on Android • iOS Coming Soon
          </p>
        </div>
      </div>
    </main>
  )
}
