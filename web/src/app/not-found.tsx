import Link from 'next/link'

export default function NotFound() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-8">
      <div className="text-center">
        <h1 className="text-6xl font-bold mb-4">🌭</h1>
        <h2 className="text-4xl font-bold mb-4">404</h2>
        <p className="text-xl mb-8 text-charcoal/70">
          Oops! This product doesn't exist.
        </p>
        <Link href="/" className="pixel-button inline-block">
          Go Home
        </Link>
      </div>
    </div>
  )
}
