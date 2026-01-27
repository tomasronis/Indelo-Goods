import { Metadata } from 'next'
import { notFound } from 'next/navigation'
import { getProducerProfile } from '@/lib/supabase'
import ProducerProfileClient from '@/components/ProducerProfileClient'

interface Props {
  params: { id: string }
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const profile = await getProducerProfile(params.id)

  if (!profile) {
    return {
      title: 'Producer Not Found - Indelo Goods',
    }
  }

  return {
    title: `${profile.company_name || profile.brand_name || 'Producer'} - Indelo Goods`,
    description: profile.bio || `${profile.company_name || profile.brand_name} - Local producer on Indelo Goods`,
  }
}

export default async function ProducerProfilePage({ params }: Props) {
  const profile = await getProducerProfile(params.id)

  if (!profile) {
    notFound()
  }

  return <ProducerProfileClient profile={profile} producerId={params.id} />
}
