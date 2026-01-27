import type { Metadata } from 'next'
import './globals.css'

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
      <body>{children}</body>
    </html>
  )
}
