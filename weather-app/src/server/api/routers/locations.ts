import { z } from "zod";

import { createTRPCRouter, publicProcedure } from "@/server/api/trpc";

export const locationsRouter = createTRPCRouter({
  hello: publicProcedure
    .input(z.object({ text: z.string() }))
    .query(({ input }) => {
      return {
        greeting: `Hello ${input.text}`,
      };
    }),

  getLocations: publicProcedure.query(({ ctx }) => {
    return ctx.db.locations.findMany();

    // return ctx.db.post.findFirst({
    //   orderBy: { createdAt: "desc" },
    // });
  }),
});
