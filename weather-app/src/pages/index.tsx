import Head from "next/head";

import { api } from "@/utils/api";
import { LocationSelector } from "@/components/locationselect";

export default function Home() {
  const { data: locations } = api.locations.getLocations.useQuery();

  return (
    <>
      <Head>
        <title>Weather In West Australia</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <main className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-[#2e026d] to-[#15162c]">
        <div className="container flex flex-col items-center justify-center gap-12 px-4 py-16 ">
          <h1 className="text-5xl font-extrabold tracking-tight text-white sm:text-[5rem]">
            Weather In Western Australia
          </h1>
          <div className="flex max-w-4xl flex-col gap-4 rounded-xl bg-white/10 p-4 text-white hover:bg-white/20">
            <LocationSelector locations={locations}></LocationSelector>
          </div>
        </div>
      </main>
    </>
  );
}
