import type { Metadata } from 'next'
import './globals.css'
import Footer from '@/components/Footer'

export const metadata: Metadata = {
  title: 'Indelo Goods - Discover Quality Products',
  description: 'Scan QR codes to discover and purchase quality canned goods and beverages from local producers.',
  keywords: 'canned goods, beverages, QR code shopping, local producers',
  openGraph: {
    title: 'Indelo Goods',
    description: 'Discover Quality Products',
    type: 'website',
  },
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className="flex flex-col min-h-screen">
        <main className="flex-1">{children}</main>
        <Footer />
      </body>
    </html>
  )
}
